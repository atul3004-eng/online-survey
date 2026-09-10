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
