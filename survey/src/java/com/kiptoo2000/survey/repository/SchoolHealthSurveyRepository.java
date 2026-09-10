package com.kiptoo2000.survey.repository;

import com.kiptoo2000.survey.model.SchoolHealthAnswer;
import com.kiptoo2000.survey.model.SchoolHealthResponse;
import com.kiptoo2000.survey.persistence.JpaUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class SchoolHealthSurveyRepository implements Serializable {

    private transient javax.persistence.EntityManagerFactory factory;
    public SchoolHealthSurveyRepository() { }
    public SchoolHealthSurveyRepository(javax.persistence.EntityManagerFactory factory) { this.factory = factory; }
    private EntityManager createEntityManager() {
        return factory == null ? JpaUtility.createEntityManager() : factory.createEntityManager();
    }

    private static final Map<String, FieldMeta> FIELD_META = createFieldMeta();
    private static final Map<String, String> AR_FIELD_LABELS = createArabicFieldLabels();

    public Long save(Map<String, String> answers, Map<String, String[]> multiAnswers) {
        return save(answers, multiAnswers, "en");
    }

    public Long save(Map<String, String> answers, Map<String, String[]> multiAnswers, String locale) {
        return save(null, java.util.UUID.randomUUID().toString(), answers, multiAnswers, locale, false);
    }

    public Long save(Long id, String token, Map<String, String> answers,
            Map<String, String[]> multiAnswers, String locale, boolean draft) {
        EntityManager entityManager = createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            SchoolHealthResponse response = id == null ? new SchoolHealthResponse()
                    : entityManager.find(SchoolHealthResponse.class, id, javax.persistence.LockModeType.PESSIMISTIC_WRITE);
            if (response == null || (id != null && (!token.equals(response.getResumeToken())
                    || !"DRAFT".equals(response.getStatus())))) {
                throw new IllegalStateException("Response is unavailable or already submitted.");
            }
            response.setResumeToken(token);
            response.setStatus(draft ? "DRAFT" : "SUBMITTED");
            response.setResponseLocale(normalizeLocale(locale));
            response.setSubmittedOn(draft ? null : new java.util.Date());
            response.getAnswers().clear();
            response.setInstitutionName(value(answers, "institutionName"));
            response.setContactEmail(value(answers, "contactEmail"));
            response.setContactPhone(value(answers, "contactPhone"));

            if (!draft && hasDuplicateContact(entityManager, response.getContactEmail(), response.getContactPhone(), id)) {
                throw new DuplicateSurveySubmissionException("This email address or mobile phone number has already submitted the school health survey.");
            }

            for (Map.Entry<String, String> entry : answers.entrySet()) {
                String answerValue = trim(entry.getValue());
                if (!answerValue.isEmpty()) {
                    response.addAnswer(answer(entry.getKey(), answerValue, locale));
                }
            }

            for (Map.Entry<String, String[]> entry : multiAnswers.entrySet()) {
                String[] selectedValues = entry.getValue();
                if (selectedValues != null) {
                    for (String selectedValue : selectedValues) {
                        String answerValue = trim(selectedValue);
                        if (!answerValue.isEmpty()) {
                            response.addAnswer(answer(entry.getKey(), answerValue, locale));
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

    public SchoolHealthResponse findByToken(String token) {
        if (token == null || !token.matches("[a-f0-9-]{36}")) { return null; }
        EntityManager em = createEntityManager();
        try {
            List<SchoolHealthResponse> rows = em.createQuery(
                    "SELECT r FROM SchoolHealthResponse r WHERE r.resumeToken = :token", SchoolHealthResponse.class)
                    .setParameter("token", token).getResultList();
            if (rows.isEmpty()) { return null; }
            SchoolHealthResponse response = rows.get(0);
            response.getAnswers().size();
            return response;
        } finally { em.close(); }
    }

    public List<SchoolHealthAnswer> questionnaire(String locale) {
        List<SchoolHealthAnswer> rows = new ArrayList<SchoolHealthAnswer>();
        for (String key : FIELD_META.keySet()) { rows.add(answer(key, "", locale)); }
        return rows;
    }

    public String findQuestionText(String questionKey, String locale) {
        EntityManager entityManager = createEntityManager();
        try {
            String text = findQuestionText(entityManager, questionKey, normalizeLocale(locale));
            if (text == null && !"en".equals(normalizeLocale(locale))) {
                text = findQuestionText(entityManager, questionKey, "en");
            }
            return text != null ? text : fallbackQuestionText(questionKey, locale);
        } catch (RuntimeException ex) {
            return fallbackQuestionText(questionKey, locale);
        } finally {
            entityManager.close();
        }
    }

    public List<OptionRow> findOptions(String optionGroup, String locale) {
        EntityManager entityManager = createEntityManager();
        try {
            List<OptionRow> rows = findOptions(entityManager, optionGroup, normalizeLocale(locale));
            if (rows.isEmpty() && !"en".equals(normalizeLocale(locale))) {
                rows = findOptions(entityManager, optionGroup, "en");
            }
            return rows;
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        } finally {
            entityManager.close();
        }
    }

    private String findQuestionText(EntityManager entityManager, String questionKey, String locale) {
        Query query = entityManager.createNativeQuery(
                "SELECT "
                        + ("ar".equals(locale) ? "COALESCE(NULLIF(SURVEY_QNS_AR, ''), SURVEY_QNS)" : "SURVEY_QNS")
                        + " FROM SURVEY_DETAILS WHERE FIELD_OPTION = ?");
        query.setParameter(1, questionKey);
        List results = query.getResultList();
        return results.isEmpty() ? null : trim(String.valueOf(results.get(0)));
    }

    private String findSectionName(EntityManager entityManager, String questionKey, String locale) {
        Query query = entityManager.createNativeQuery(
                "SELECT "
                        + ("ar".equals(locale) ? "COALESCE(NULLIF(SURVEY_HEADING_AR, ''), SURVEY_HEADING)" : "SURVEY_HEADING")
                        + " FROM SURVEY_DETAILS WHERE FIELD_OPTION = ?");
        query.setParameter(1, questionKey);
        List results = query.getResultList();
        return results.isEmpty() ? null : trim(String.valueOf(results.get(0)));
    }

    private List<OptionRow> findOptions(EntityManager entityManager, String optionGroup, String locale) {
        Query query = entityManager.createNativeQuery(
                "SELECT ID, DESCRIPTION, "
                        + ("ar".equals(locale) ? "COALESCE(NULLIF(DESCRIPTION_AR, ''), DESCRIPTION)" : "DESCRIPTION")
                        + " FROM OPTIONS WHERE OPTION_TYPE = ? ORDER BY ID");
        query.setParameter(1, optionGroup);
        List rows = query.getResultList();
        if (rows.isEmpty()) {
            return Collections.emptyList();
        }
        List<OptionRow> options = new ArrayList<OptionRow>();
        for (Object row : rows) {
            Object[] values = (Object[]) row;
            String value = optionValue(optionGroup, String.valueOf(values[1]), String.valueOf(values[0]));
            options.add(new OptionRow(value, String.valueOf(values[2])));
        }
        return options;
    }

    private SchoolHealthAnswer answer(String key, String value, String locale) {
        FieldMeta meta = FIELD_META.get(key);
        SchoolHealthAnswer answer = new SchoolHealthAnswer();
        answer.setQuestionKey(key);
        answer.setAnswerValue(value);
        answer.setSectionName(sectionName(key, locale, meta));
        answer.setQuestionLabel(findQuestionText(key, locale));
        answer.setDisplayOrder(meta != null ? meta.order : 9999);
        return answer;
    }

    private String sectionName(String key, String locale, FieldMeta meta) {
        EntityManager entityManager = createEntityManager();
        try {
            String section = findSectionName(entityManager, key, normalizeLocale(locale));
            if (section == null && !"en".equals(normalizeLocale(locale))) {
                section = findSectionName(entityManager, key, "en");
            }
            if (section != null) {
                return section;
            }
        } catch (RuntimeException ex) {
            // Fall back to static metadata below.
        } finally {
            entityManager.close();
        }
        return meta != null ? meta.section : "Other";
    }

    private String optionValue(String optionGroup, String label, String id) {
        String code = label == null ? "" : label.toLowerCase().replaceAll("[^a-z0-9]+", "_");
        code = code.replaceAll("^_+", "").replaceAll("_+$", "");
        if (code.length() == 0) {
            code = id;
        }
        if (isOtherLabel(label)) {
            return "other";
        }
        return code;
    }

    private boolean isOtherLabel(String label) {
        String text = trim(label).toLowerCase();
        return "other".equals(text) || text.contains("خيارات") || text.contains("أخرى");
    }

    private static String fallbackQuestionText(String questionKey, String locale) {
        if ("ar".equals(normalizeLocale(locale)) && AR_FIELD_LABELS.containsKey(questionKey)) {
            return AR_FIELD_LABELS.get(questionKey);
        }
        FieldMeta meta = FIELD_META.get(questionKey);
        return meta != null ? meta.label : questionKey;
    }

    private static String normalizeLocale(String locale) {
        return "ar".equalsIgnoreCase(trim(locale)) ? "ar" : "en";
    }

    private static String value(Map<String, String> answers, String key) {
        return answers.containsKey(key) ? trim(answers.get(key)) : null;
    }

    private boolean hasDuplicateContact(EntityManager entityManager, String email, String phone, Long id) {
        boolean hasEmail = !trim(email).isEmpty();
        boolean hasPhone = !trim(phone).isEmpty();
        if (!hasEmail && !hasPhone) {
            return false;
        }

        StringBuilder jpql = new StringBuilder("SELECT COUNT(r) FROM SchoolHealthResponse r WHERE r.status = 'SUBMITTED' AND (:id IS NULL OR r.id <> :id) AND (");
        if (hasEmail) {
            jpql.append("LOWER(r.contactEmail) = LOWER(:email)");
        }
        if (hasPhone) {
            if (hasEmail) {
                jpql.append(" OR ");
            }
            jpql.append("r.contactPhone = :phone");
        }

        jpql.append(")");
        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
        query.setParameter("id", id);
        if (hasEmail) {
            query.setParameter("email", email);
        }
        if (hasPhone) {
            query.setParameter("phone", phone);
        }
        return query.getSingleResult() > 0;
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private static Map<String, FieldMeta> createFieldMeta() {
        Map<String, FieldMeta> meta = new LinkedHashMap<String, FieldMeta>();
        int order = 1;
        order = addGeneral(meta, order);
        order = addProgram(meta, order);
        order = addGovernance(meta, order);
        order = addDocuments(meta, order);
        order = addResources(meta, order);
        addFeedback(meta, order);
        return meta;
    }

    private static Map<String, String> createArabicFieldLabels() {
        Map<String, String> labels = new LinkedHashMap<String, String>();
        labels.put("institutionName", "اسم المؤسسة");
        labels.put("institutionType", "نوع المؤسسة");
        labels.put("institutionTypeOther", "خيارات إضافية");
        labels.put("department", "الإدارة / القسم");
        labels.put("contactName", "اسم جهة الاتصال");
        labels.put("contactTitle", "المسمى الوظيفي");
        labels.put("contactEmail", "البريد الإلكتروني");
        labels.put("contactPhone", "رقم الهاتف");
        labels.put("programTitle", "اسم البرنامج / المبادرة / النشاط");
        labels.put("programDescription", "وصف مختصر للبرنامج / المبادرة / النشاط");
        labels.put("currentStatus", "الوضع الراهن");
        labels.put("statusDetail", "سنة البدء أو تاريخ البدء المتوقع أو تفاصيل أخرى");
        labels.put("targetedTopics", "المواضيع المستهدفة");
        labels.put("targetedTopicOther", "خيارات إضافية للمواضيع المستهدفة");
        labels.put("targetPopulation", "الفئة المستهدفة");
        labels.put("targetPopulationOther", "خيارات إضافية للفئة المستهدفة");
        labels.put("primaryGrades", "ابتدائي");
        labels.put("preparatoryGrades", "إعدادي");
        labels.put("secondaryGrades", "ثانوي");
        labels.put("coverage", "نطاق التغطية");
        labels.put("schoolsCovered", "عدد المدارس المغطاة");
        labels.put("coverageInfo", "أي إضافات أخرى متعلقة بنطاق التغطية");
        labels.put("performanceIndicators", "هل لدى مؤسستكم مؤشرات أداء لمتابعة وتقييم هذه البرامج؟");
        labels.put("performanceIndicatorsDescription", "إذا كانت الإجابة نعم، يرجى الوصف بإيجاز");
        labels.put("partnersInvolved", "هل توجد جهات شريكة في التنفيذ؟");
        labels.put("partnersSpecification", "إذا كانت الإجابة نعم، يرجى التحديد");
        labels.put("partnerNames", "اسم الشريك / الشركاء");
        labels.put("partnerRoles", "دور الشريك / الشركاء");
        labels.put("coordinationMechanism", "هل توجد آلية تنسيق قائمة؟");
        labels.put("coordinationStructure", "يرجى ذكر اسم الجهة وهيكلها التنظيمي");
        labels.put("documentsDeveloped", "هل قامت مؤسستكم بإعداد أو المساهمة في تطوير وثائق داعمة لهذا النشاط؟");
        labels.put("documentTitlesYears", "يرجى ذكر عناوين وسنوات إصدار الوثائق");
        labels.put("humanResources", "الموارد البشرية المخصصة");
        labels.put("humanResourceOther", "خيارات إضافية للموارد البشرية");
        labels.put("capacityBuilding", "هل يتم تنفيذ أنشطة لبناء القدرات أو تدريب للكوادر المحددة أعلاه؟");
        labels.put("trainingFrequency", "عدد مرات التدريب");
        labels.put("trainingFrequencyOther", "خيارات إضافية لعدد مرات التدريب");
        labels.put("infrastructureSupport", "البنية التحتية الداعمة المتوفرة");
        labels.put("infrastructureOther", "خيارات إضافية للبنية التحتية");
        labels.put("programFeedback", "ملاحظات أو توصيات لتعزيز برامج تعزيز الصحة في المدارس الحالية");
        labels.put("nationalContribution", "كيف يمكن لمؤسستكم المساهمة في المبادرة الوطنية للمدارس المعززة للصحة؟");
        return labels;
    }

    private static int addGeneral(Map<String, FieldMeta> meta, int order) {
        String section = "General Information";
        add(meta, "institutionName", section, "Institution name", order++);
        add(meta, "institutionType", section, "Type of institution", order++);
        add(meta, "institutionTypeOther", section, "Other institution type", order++);
        add(meta, "department", section, "Department / section", order++);
        add(meta, "contactName", section, "Contact person name", order++);
        add(meta, "contactTitle", section, "Contact person title", order++);
        add(meta, "contactEmail", section, "Contact email", order++);
        add(meta, "contactPhone", section, "Contact phone number", order++);
        return order;
    }

    private static int addProgram(Map<String, FieldMeta> meta, int order) {
        String section = "School Health Promotion Program / Initiative / Activity";
        add(meta, "programTitle", section, "Program / initiative / activity title", order++);
        add(meta, "programDescription", section, "Brief description", order++);
        add(meta, "currentStatus", section, "Current status", order++);
        add(meta, "statusDetail", section, "Status year / date / detail", order++);
        add(meta, "targetedTopics", section, "Targeted topics", order++);
        add(meta, "targetedTopicOther", section, "Other targeted topic", order++);
        add(meta, "targetPopulation", section, "Target population", order++);
        add(meta, "targetPopulationOther", section, "Other target population", order++);
        add(meta, "primaryGrades", section, "Primary grade levels", order++);
        add(meta, "preparatoryGrades", section, "Preparatory grade levels", order++);
        add(meta, "secondaryGrades", section, "Secondary grade levels", order++);
        add(meta, "coverage", section, "Coverage", order++);
        add(meta, "schoolsCovered", section, "Number of schools covered", order++);
        add(meta, "coverageInfo", section, "Other coverage information", order++);
        add(meta, "performanceIndicators", section, "Performance indicators status", order++);
        add(meta, "performanceIndicatorsDescription", section, "Performance indicators description", order++);
        return order;
    }

    private static int addGovernance(Map<String, FieldMeta> meta, int order) {
        String section = "Governance, Coordination, and Partnerships";
        add(meta, "partnersInvolved", section, "Partners involved", order++);
        add(meta, "partnersSpecification", section, "Partner details", order++);
        add(meta, "partnerNames", section, "Partner names", order++);
        add(meta, "partnerRoles", section, "Partner roles", order++);
        add(meta, "coordinationMechanism", section, "Established coordination mechanism", order++);
        add(meta, "coordinationStructure", section, "Coordination name and structure", order++);
        return order;
    }

    private static int addDocuments(Map<String, FieldMeta> meta, int order) {
        String section = "Policies, Guidelines, and Documents";
        add(meta, "documentsDeveloped", section, "Documents developed or contributed", order++);
        add(meta, "documentTitlesYears", section, "Document titles and publication years", order++);
        return order;
    }

    private static int addResources(Map<String, FieldMeta> meta, int order) {
        String section = "Resources and Capacity";
        add(meta, "humanResources", section, "Dedicated human resources", order++);
        add(meta, "humanResourceOther", section, "Other human resource", order++);
        add(meta, "capacityBuilding", section, "Capacity building or training", order++);
        add(meta, "trainingFrequency", section, "Training frequency", order++);
        add(meta, "trainingFrequencyOther", section, "Other training frequency", order++);
        add(meta, "infrastructureSupport", section, "Infrastructure support", order++);
        add(meta, "infrastructureOther", section, "Other infrastructure support", order++);
        return order;
    }

    private static int addFeedback(Map<String, FieldMeta> meta, int order) {
        String section = "Feedback / Recommendations";
        add(meta, "programFeedback", section, "Feedback or recommendations", order++);
        add(meta, "nationalContribution", section, "Contribution to Health-Promoting Schools initiative", order++);
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

    public static class OptionRow {
        private final String value;
        private final String label;

        public OptionRow(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }
}
