package com.kiptoo2000.survey.repository;

import com.kiptoo2000.survey.model.WellnessAnswer;
import com.kiptoo2000.survey.model.WellnessResponse;
import com.kiptoo2000.survey.persistence.JpaUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class WellnessSurveyRepository implements Serializable {

    private static final Map<String, FieldMeta> FIELD_META = createFieldMeta();

    public Long save(Map<String, String> answers, Map<String, String[]> multiAnswers) {
        EntityManager entityManager = JpaUtility.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            WellnessResponse response = new WellnessResponse();
            response.setCompanyName(value(answers, "companyName"));
            response.setEmail(value(answers, "email"));
            response.setPhone(value(answers, "phone"));

            for (Map.Entry<String, String> entry : answers.entrySet()) {
                String answerValue = trim(entry.getValue());
                if (!answerValue.isEmpty()) {
                    response.addAnswer(answer(entry.getKey(), answerValue));
                }
            }

            for (Map.Entry<String, String[]> entry : multiAnswers.entrySet()) {
                String[] selectedValues = entry.getValue();
                if (selectedValues != null) {
                    for (String selectedValue : selectedValues) {
                        String answerValue = trim(selectedValue);
                        if (!answerValue.isEmpty()) {
                            response.addAnswer(answer(entry.getKey(), answerValue));
                        }
                    }
                }
            }

            entityManager.persist(response);
            transaction.commit();
            return response.getId();
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    public List<WellnessResponse> findAllResponses() {
        EntityManager entityManager = JpaUtility.createEntityManager();
        try {
            return entityManager.createQuery(
                    "SELECT r FROM WellnessResponse r ORDER BY r.submittedOn DESC", WellnessResponse.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public WellnessResponse findResponse(Long responseId) {
        EntityManager entityManager = JpaUtility.createEntityManager();
        try {
            return entityManager.find(WellnessResponse.class, responseId);
        } finally {
            entityManager.close();
        }
    }

    public List<WellnessAnswer> findAnswers(Long responseId) {
        if (responseId == null) {
            return new ArrayList<WellnessAnswer>();
        }
        EntityManager entityManager = JpaUtility.createEntityManager();
        try {
            return entityManager.createQuery(
                    "SELECT a FROM WellnessAnswer a WHERE a.response.id = :responseId ORDER BY a.displayOrder, a.id",
                    WellnessAnswer.class)
                    .setParameter("responseId", responseId)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    private WellnessAnswer answer(String key, String value) {
        FieldMeta meta = FIELD_META.get(key);
        WellnessAnswer answer = new WellnessAnswer();
        answer.setQuestionKey(key);
        answer.setAnswerValue(value);
        answer.setSectionName(meta != null ? meta.section : "Other");
        answer.setQuestionLabel(meta != null ? meta.label : key);
        answer.setDisplayOrder(meta != null ? meta.order : 9999);
        return answer;
    }

    private static String value(Map<String, String> answers, String key) {
        return answers.containsKey(key) ? trim(answers.get(key)) : null;
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private static Map<String, FieldMeta> createFieldMeta() {
        Map<String, FieldMeta> meta = new LinkedHashMap<String, FieldMeta>();
        int order = 1;
        order = addCompany(meta, order);
        order = addOhs(meta, order);
        order = addMedical(meta, order);
        addWellness(meta, order);
        return meta;
    }

    private static int addCompany(Map<String, FieldMeta> meta, int order) {
        String section = "Company Profile";
        add(meta, "companyName", section, "Company name", order++);
        add(meta, "address", section, "Address", order++);
        add(meta, "email", section, "Email address", order++);
        add(meta, "phone", section, "Phone number", order++);
        add(meta, "focalCompany", section, "Focal point company name", order++);
        add(meta, "focalName", section, "Focal point name", order++);
        add(meta, "jobTitle", section, "Job title", order++);
        add(meta, "focalEmail", section, "Focal point email", order++);
        add(meta, "officePhone", section, "Office phone number", order++);
        add(meta, "mobilePhone", section, "Mobile phone number", order++);
        add(meta, "industry", section, "Type of industry", order++);
        add(meta, "industryOther", section, "Other industry / company activities", order++);
        add(meta, "fullTime", section, "Full-time employees", order++);
        add(meta, "partTime", section, "Part-time employees", order++);
        add(meta, "contracted", section, "Contracted/outsourced employees", order++);
        add(meta, "totalEmployees", section, "Total employees", order++);
        add(meta, "outsourcedServices", section, "Contracted/outsourced service types", order++);
        add(meta, "outsourcedOther", section, "Other outsourced service", order++);
        add(meta, "occupations", section, "Main occupations", order++);
        add(meta, "occupationOther", section, "Other occupation", order++);
        add(meta, "ageLt18", section, "Employees less than 18", order++);
        add(meta, "age18", section, "Employees age 18 - 24", order++);
        add(meta, "age25", section, "Employees age 25 - 44", order++);
        add(meta, "age45", section, "Employees age 45 - 64", order++);
        add(meta, "age65", section, "Employees age 65 and older", order++);
        add(meta, "male", section, "Male employees", order++);
        add(meta, "female", section, "Female employees", order++);
        add(meta, "qatari", section, "Qatari employees", order++);
        add(meta, "nonQatari", section, "Non-Qatari employees", order++);
        add(meta, "countries", section, "Countries for non-Qatari employees", order++);
        add(meta, "countryOther", section, "Other country", order++);
        return order;
    }

    private static int addOhs(Map<String, FieldMeta> meta, int order) {
        String section = "Occupational Health and Safety";
        add(meta, "ohsPolicy", section, "OHS policy/document", order++);
        add(meta, "ohsPolicyAspects", section, "Policy aspects covered", order++);
        add(meta, "ohsPolicyOther", section, "Other policy aspect", order++);
        add(meta, "responsibleOfficer", section, "Designated responsible officer", order++);
        add(meta, "ohsCommittee", section, "Dedicated OHS department/committee", order++);
        add(meta, "discussionFrequency", section, "Health and safety discussion frequency", order++);
        add(meta, "riskAssessment", section, "Working environment surveillance / risk assessment", order++);
        add(meta, "hazards", section, "Hazards routinely evaluated", order++);
        add(meta, "hazardOther", section, "Other hazard", order++);
        add(meta, "riskFrequency", section, "Risk assessment frequency", order++);
        add(meta, "riskProgram", section, "Risk program and control measures", order++);
        add(meta, "riskDocumented", section, "Risk assessments documented", order++);
        add(meta, "hygieneRecords", section, "Hygiene surveillance records", order++);
        add(meta, "trainingPlan", section, "OHS training plan status", order++);
        add(meta, "ohsTrainingItems", section, "OHS training plan items", order++);
        add(meta, "trainingOther", section, "Other training item", order++);
        return order;
    }

    private static int addMedical(Map<String, FieldMeta> meta, int order) {
        String section = "Health Assessment and Medical Examination";
        add(meta, "firstAidBoxes", section, "First aid boxes", order++);
        add(meta, "firstAidUnit", section, "First aid unit", order++);
        add(meta, "companyClinic", section, "Company clinic", order++);
        add(meta, "generalPractitioner", section, "General practitioner", order++);
        add(meta, "occupationalPhysician", section, "Specialist occupational physician", order++);
        add(meta, "occupationalNurse", section, "Occupational health nurse", order++);
        add(meta, "preEmploymentExam", section, "Pre-employment health examination", order++);
        add(meta, "preEmploymentOther", section, "Other pre-employment detail", order++);
        add(meta, "periodicExam", section, "Periodic medical examinations", order++);
        add(meta, "periodicExamOther", section, "Other periodic examination detail", order++);
        add(meta, "lifestyleSurveys", section, "Lifestyle and behavior surveys", order++);
        return order;
    }

    private static int addWellness(Map<String, FieldMeta> meta, int order) {
        String section = "Health Promotion / Workplace Wellness";
        add(meta, "wellnessPolicy", section, "Workplace wellness policy", order++);
        add(meta, "wellnessPolicyElements", section, "Wellness policy elements", order++);
        add(meta, "wellnessPolicyOther", section, "Other policy element", order++);
        add(meta, "wellnessProgram", section, "Workplace wellness program", order++);
        add(meta, "wellnessStrategies", section, "Wellness program strategies", order++);
        add(meta, "wellnessOwners", section, "Wellness program responsible body", order++);
        add(meta, "wellnessOwnerOther", section, "Other responsible body", order++);
        add(meta, "wellnessProgramElements", section, "Wellness program elements", order++);
        add(meta, "wellnessProgramOther", section, "Other program element", order++);
        add(meta, "communicationMethods", section, "Healthy behavior communication methods", order++);
        add(meta, "communicationOther", section, "Other communication method", order++);
        add(meta, "freeHealthyFood", section, "Healthy food and beverages free of charge", order++);
        add(meta, "cafeteria", section, "Company cafeteria/canteen", order++);
        add(meta, "cafeteriaPromotion", section, "Cafeteria promotes healthy food", order++);
        add(meta, "physicalActivityFacilities", section, "On-site physical activity facilities", order++);
        add(meta, "tobaccoBan", section, "Tobacco-use ban", order++);
        add(meta, "mentalHealthSupport", section, "Counseling / mental health support", order++);
        return order;
    }

    private static void add(Map<String, FieldMeta> meta, String key, String section, String label, int order) {
        meta.put(key, new FieldMeta(section, label, order));
    }

    private static class FieldMeta {
        private final String section;
        private final String label;
        private final Integer order;

        private FieldMeta(String section, String label, Integer order) {
            this.section = section;
            this.label = label;
            this.order = order;
        }
    }
}
