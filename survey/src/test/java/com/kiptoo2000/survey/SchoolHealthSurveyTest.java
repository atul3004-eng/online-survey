package com.kiptoo2000.survey;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.nio.file.*;
import com.kiptoo2000.survey.repository.*;
import com.kiptoo2000.survey.model.*;
import com.kiptoo2000.survey.bean.*;
import com.kiptoo2000.survey.pdf.*;
import com.lowagie.text.pdf.PdfReader;

public class SchoolHealthSurveyTest {
    @Test public void completedPdfOmitsUnansweredQuestionsAndEmptySections() throws Exception {
        SchoolHealthResponse response = new SchoolHealthResponse();
        java.util.List<SchoolHealthAnswer> questions = new ArrayList<SchoolHealthAnswer>();
        String[] keys = {"missing", "blank", "nullValue", "schoolsCovered", "partnersInvolved", "targetedTopics"};
        String[] labels = {"Missing question", "Blank question", "Null question", "School count", "Partners question", "Topics question"};
        for (int i = 0; i < keys.length; i++) {
            SchoolHealthAnswer question = new SchoolHealthAnswer();
            question.setQuestionKey(keys[i]);
            question.setQuestionLabel(labels[i]);
            question.setSectionName(i < 3 ? "Unanswered section" : "Answered section");
            questions.add(question);
        }
        String[][] saved = {{"blank", " \t "}, {"nullValue", null}, {"schoolsCovered", "0"},
            {"partnersInvolved", "no"}, {"targetedTopics", ""}, {"targetedTopics", "nutrition"}, {"targetedTopics", "mental_health"}};
        for (String[] entry : saved) {
            SchoolHealthAnswer answer = new SchoolHealthAnswer();
            answer.setQuestionKey(entry[0]); answer.setAnswerValue(entry[1]); response.addAnswer(answer);
        }
        byte[] pdf = new SchoolHealthPdf().generate(response, questions, new SchoolHealthSurveyBean() {
            @Override public java.util.List<javax.faces.model.SelectItem> options(String group) {
                return Arrays.asList(new javax.faces.model.SelectItem("no", "No"),
                    new javax.faces.model.SelectItem("nutrition", "Nutrition"),
                    new javax.faces.model.SelectItem("mental_health", "Mental health"));
            }
        });
        PdfReader reader = new PdfReader(pdf);
        StringBuilder text = new StringBuilder();
        com.lowagie.text.pdf.parser.PdfTextExtractor extractor = new com.lowagie.text.pdf.parser.PdfTextExtractor(reader);
        for (int page = 1; page <= reader.getNumberOfPages(); page++) { text.append(extractor.getTextFromPage(page)); }
        reader.close();
        String content = text.toString();
        for (String omitted : new String[]{"Missing question", "Blank question", "Null question", "Unanswered section", "Not answered"}) {
            assertFalse(omitted, content.contains(omitted));
        }
        for (String included : new String[]{"Answered section", "School count", "0", "Partners question", "No", "Topics question", "Nutrition; Mental health"}) {
            assertTrue(included, content.contains(included));
        }
    }

    @Test public void infrastructureChoicesAndOtherSelectionInBothLanguages() {
        SchoolHealthSurveyBean bean = new SchoolHealthSurveyBean();
        for (String locale : Arrays.asList("en", "ar")) {
            bean.setLocale(locale);
            java.util.List<javax.faces.model.SelectItem> options = bean.options("infrastructureSupports");
            assertEquals(6, options.size());
            assertEquals("educational_materials", options.get(0).getValue());
            assertEquals("other", options.get(5).getValue());
            bean.getMultiAnswers().put("infrastructureSupport", new String[]{"school_clinics", "other"});
            assertTrue(bean.isOtherSelected("infrastructureSupport"));
            bean.getMultiAnswers().put("infrastructureSupport", new String[]{"school_clinics"});
            assertFalse(bean.isOtherSelected("infrastructureSupport"));
        }
    }

    @Test public void draftLifecycleAndCompletedPdf() throws Exception {
        SchoolHealthSurveyRepository repository = new SchoolHealthSurveyRepository(javax.persistence.Persistence.createEntityManagerFactory("schoolHealthTestPU"));
        String token = UUID.randomUUID().toString();
        Map<String,String> answers = new LinkedHashMap<String,String>();
        Map<String,String[]> multi = new LinkedHashMap<String,String[]>();
        answers.put("institutionName", "Test school");
        multi.put("targetedTopics", new String[]{"nutrition", "mental_health"});
        Long id = repository.save(null, token, answers, multi, "en", true);
        SchoolHealthResponse draft = repository.findByToken(token);
        assertEquals("DRAFT", draft.getStatus()); assertNull(draft.getSubmittedOn());
        assertEquals(3, draft.getAnswers().size());
        assertNull(repository.findByToken(UUID.randomUUID().toString()));
        answers.put("contactEmail", "test@example.com");
        answers.put("programFeedback", "مدرسة صحية - Arabic response");
        multi.put("targetedTopics", new String[]{"nutrition"});
        assertEquals(id, repository.save(id, token, answers, multi, "en", true));
        assertEquals(4, repository.findByToken(token).getAnswers().size());
        assertEquals(id, repository.save(id, token, answers, multi, "en", false));
        SchoolHealthResponse submitted = repository.findByToken(token);
        assertEquals("SUBMITTED", submitted.getStatus()); assertNotNull(submitted.getSubmittedOn());
        try { repository.save(id, token, answers, multi, "en", true); fail("Submitted responses must be immutable"); }
        catch (IllegalStateException expected) { }
        try { repository.save(null, UUID.randomUUID().toString(), answers, multi, "en", false); fail("Duplicate submission"); }
        catch (DuplicateSurveySubmissionException expected) { }
        Long other = repository.save(null, UUID.randomUUID().toString(), answers, multi, "en", true);
        assertNotEquals(id, other);
        byte[] pdf = new SchoolHealthPdf().generate(submitted, repository.questionnaire("en"), new SchoolHealthSurveyBean() {
            @Override public java.util.List<javax.faces.model.SelectItem> options(String group) {
                return Arrays.asList(new javax.faces.model.SelectItem("nutrition", "Nutrition"));
            }
        });
        PdfReader reader = new PdfReader(pdf); assertTrue(reader.getNumberOfPages() > 0); reader.close();
        Files.write(Paths.get("target/school-health-completed-test.pdf"), pdf);
        assertNotNull(getClass().getResourceAsStream("/docs/school-health-questionnaire.pdf"));
    }
}
