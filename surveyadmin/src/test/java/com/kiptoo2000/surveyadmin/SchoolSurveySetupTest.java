package com.kiptoo2000.surveyadmin;

import com.kiptoo2000.surveyadmin.model.*;
import com.kiptoo2000.surveyadmin.repository.SchoolSurveySetupRepository;
import javax.persistence.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SchoolSurveySetupTest {
    @Test public void crudUsesManualIdsAndRejectsDuplicatesAndMissingUpdates() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("exportTest");
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("CREATE TABLE SURVEY_DETAILS (ID BIGINT PRIMARY KEY, EVENT_UUID VARCHAR(100), SURVEY_HEADING_ID BIGINT, SURVEY_HEADING VARCHAR(255), SURVEY_QNS VARCHAR(1000), FIELD_TYPE VARCHAR(50), IS_MANDATORY BOOLEAN, FIELD_OPTION VARCHAR(100), REQUIRED_MESSAGE VARCHAR(500), PRE_SURVEY BOOLEAN, POST_SURVEY BOOLEAN, CORRECT_ANSWER_ID BIGINT, SURVEY_HEADING_AR VARCHAR(255), SURVEY_QNS_AR VARCHAR(1000), REQUIRED_MESSAGE_AR VARCHAR(500))").executeUpdate();
            em.createNativeQuery("CREATE TABLE OPTIONS (ID INT PRIMARY KEY, DESCRIPTION VARCHAR(500), DESCRIPTION_AR VARCHAR(500), EVENT_UUID VARCHAR(100), OPTION_TYPE VARCHAR(100))").executeUpdate();
            em.getTransaction().commit();
            SchoolSurveySetupRepository repository = new SchoolSurveySetupRepository(factory);
            SchoolSurveyDetail detail = new SchoolSurveyDetail();
            detail.setId(901L); detail.setHeading("General"); detail.setQuestion("Institution name");
            detail.setFieldType("text"); detail.setFieldOption("institutionName");
            detail.setQuestionAr("اسم المؤسسة"); detail.setMandatory(true); detail.setPreSurvey(true);
            repository.insertDetail(detail);
            SchoolSurveyDetail saved = repository.findDetails().get(0);
            assertEquals(Long.valueOf(901), saved.getId()); assertTrue(saved.isMandatory());
            assertEquals("اسم المؤسسة", saved.getQuestionAr()); assertNull(saved.getHeadingId());
            detail.setQuestion("Updated question"); detail.setMandatory(false);
            repository.saveDetail(detail, true);
            assertEquals("Updated question", repository.findDetails().get(0).getQuestion());
            assertFalse(repository.findDetails().get(0).isMandatory());
            try { repository.insertDetail(detail); fail("Duplicate ID accepted"); }
            catch (IllegalArgumentException expected) { assertTrue(expected.getMessage().contains("already exists")); }
            assertEquals(1, repository.findDetails().size());
            SchoolSurveyOption option = new SchoolSurveyOption();
            option.setId(902L); option.setDescription("Other"); option.setDescriptionAr("أخرى"); option.setOptionType("institutionTypes");
            repository.insertOption(option);
            assertEquals("أخرى", repository.findOptions().get(0).getDescriptionAr());
            option.setDescription("Updated option"); repository.saveOption(option, true);
            assertEquals("Updated option", repository.findOptions().get(0).getDescription());
            try { repository.insertOption(option); fail("Duplicate option accepted"); }
            catch (IllegalArgumentException expected) { assertTrue(expected.getMessage().contains("already exists")); }
            repository.deleteOption(option.getId()); repository.deleteDetail(detail.getId());
            assertTrue(repository.findOptions().isEmpty()); assertTrue(repository.findDetails().isEmpty());
            try { repository.saveDetail(detail, true); fail("Missing row update accepted"); }
            catch (IllegalArgumentException expected) { assertTrue(expected.getMessage().contains("no longer exists")); }
            option.setId(null);
            try { repository.insertOption(option); fail("Missing ID accepted"); }
            catch (IllegalArgumentException expected) { assertTrue(expected.getMessage().contains("positive ID")); }
        } finally {
            if (em.getTransaction().isActive()) { em.getTransaction().rollback(); }
            em.getTransaction().begin();
            em.createNativeQuery("DROP TABLE IF EXISTS OPTIONS").executeUpdate();
            em.createNativeQuery("DROP TABLE IF EXISTS SURVEY_DETAILS").executeUpdate();
            em.getTransaction().commit(); em.close(); factory.close();
        }
    }
}
