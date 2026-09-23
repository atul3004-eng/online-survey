package com.kiptoo2000.surveyadmin.repository;

import com.kiptoo2000.surveyadmin.model.SchoolSurveyDetail;
import com.kiptoo2000.surveyadmin.model.SchoolSurveyOption;
import com.kiptoo2000.surveyadmin.persistence.JpaUtil;
import java.io.Serializable;
import javax.persistence.*;

public class SchoolSurveySetupRepository implements Serializable {
    private transient EntityManagerFactory factory;
    public SchoolSurveySetupRepository() { }
    public SchoolSurveySetupRepository(EntityManagerFactory factory) { this.factory = factory; }

    public java.util.List<SchoolSurveyDetail> findDetails() {
        EntityManager em = entityManager();
        try {
            java.util.List<SchoolSurveyDetail> result = new java.util.ArrayList<SchoolSurveyDetail>();
            for (Object raw : em.createNativeQuery("SELECT ID, EVENT_UUID, SURVEY_HEADING_ID, SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, CORRECT_ANSWER_ID, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR FROM SURVEY_DETAILS ORDER BY ID").getResultList()) {
                Object[] v = (Object[]) raw;
                SchoolSurveyDetail r = new SchoolSurveyDetail();
                r.setId(number(v[0])); r.setEventUuid(string(v[1])); r.setHeadingId(number(v[2]));
                r.setHeading(string(v[3])); r.setQuestion(string(v[4])); r.setFieldType(string(v[5]));
                r.setMandatory(flag(v[6])); r.setFieldOption(string(v[7])); r.setRequiredMessage(string(v[8]));
                r.setPreSurvey(flag(v[9])); r.setPostSurvey(flag(v[10])); r.setCorrectAnswerId(number(v[11]));
                r.setHeadingAr(string(v[12])); r.setQuestionAr(string(v[13])); r.setRequiredMessageAr(string(v[14]));
                result.add(r);
            }
            return result;
        } finally { em.close(); }
    }

    public java.util.List<SchoolSurveyOption> findOptions() {
        EntityManager em = entityManager();
        try {
            java.util.List<SchoolSurveyOption> result = new java.util.ArrayList<SchoolSurveyOption>();
            for (Object raw : em.createNativeQuery("SELECT ID, DESCRIPTION, DESCRIPTION_AR, EVENT_UUID, OPTION_TYPE FROM OPTIONS ORDER BY ID").getResultList()) {
                Object[] v = (Object[]) raw;
                SchoolSurveyOption r = new SchoolSurveyOption();
                r.setId(number(v[0])); r.setDescription(string(v[1])); r.setDescriptionAr(string(v[2]));
                r.setEventUuid(string(v[3])); r.setOptionType(string(v[4])); result.add(r);
            }
            return result;
        } finally { em.close(); }
    }

    public void deleteDetail(Long id) { delete("SURVEY_DETAILS", id); }
    public void deleteOption(Long id) { delete("OPTIONS", id); }
    private void delete(String table, Long id) {
        EntityManager em = entityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int changed = em.createNativeQuery("DELETE FROM " + table + " WHERE ID = ?1").setParameter(1, id).executeUpdate();
            if (changed != 1) { throw new IllegalArgumentException("Record no longer exists. Refresh the list."); }
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) { tx.rollback(); }
            throw ex;
        } finally { em.close(); }
    }
    private EntityManager entityManager() { return factory == null ? JpaUtil.createEntityManager() : factory.createEntityManager(); }
    private static Long number(Object value) { return value == null ? null : ((Number) value).longValue(); }
    private static String string(Object value) { return value == null ? null : value.toString(); }
    private static boolean flag(Object value) { return Boolean.TRUE.equals(value) || "1".equals(string(value)); }

    public void insertDetail(SchoolSurveyDetail row) { saveDetail(row, false); }
    public void saveDetail(SchoolSurveyDetail row, boolean editing) {
        required(row.getHeading(), 255, "Heading");
        required(row.getQuestion(), 1000, "Question");
        required(row.getFieldType(), 50, "Field type");
        required(row.getFieldOption(), 100, "Question key");
        save("SURVEY_DETAILS", editing, row.getId(),
                "ID, EVENT_UUID, SURVEY_HEADING_ID, SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, CORRECT_ANSWER_ID, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR",
                row.getId(), row.getEventUuid(), row.getHeadingId(), row.getHeading(), row.getQuestion(),
                row.getFieldType(), row.isMandatory() ? 1 : 0, row.getFieldOption(), row.getRequiredMessage(),
                row.isPreSurvey() ? 1 : 0, row.isPostSurvey() ? 1 : 0, row.getCorrectAnswerId(),
                row.getHeadingAr(), row.getQuestionAr(), row.getRequiredMessageAr());
    }

    public void insertOption(SchoolSurveyOption row) { saveOption(row, false); }
    public void saveOption(SchoolSurveyOption row, boolean editing) {
        required(row.getDescription(), 500, "Description");
        required(row.getOptionType(), 100, "Option group");
        if (row.getId() != null && row.getId() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Option ID must be at most 2147483647.");
        }
        save("OPTIONS", editing, row.getId(), "ID, DESCRIPTION, DESCRIPTION_AR, EVENT_UUID, OPTION_TYPE",
                row.getId(), row.getDescription(), row.getDescriptionAr(), row.getEventUuid(), row.getOptionType());
    }

    private void save(String table, boolean editing, Long id, String columns, Object... values) {
        if (id == null || id < 1) { throw new IllegalArgumentException("Enter a positive ID."); }
        EntityManager em = factory == null ? JpaUtil.createEntityManager() : factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Number count = (Number) em.createNativeQuery("SELECT COUNT(*) FROM " + table + " WHERE ID = ?1")
                    .setParameter(1, id).getSingleResult();
            if (!editing && count.longValue() > 0) { throw new IllegalArgumentException("ID " + id + " already exists in " + table + ". Choose another ID."); }
            if (editing && count.longValue() == 0) { throw new IllegalArgumentException("Record no longer exists. Refresh the list."); }
            StringBuilder placeholders = new StringBuilder();
            for (int i = 1; i <= values.length; i++) {
                if (i > 1) { placeholders.append(", "); }
                placeholders.append('?').append(i);
            }
            String sql = "INSERT INTO " + table + " (" + columns + ") VALUES (" + placeholders + ")";
            if (editing) {
                String[] names = columns.split(", ");
                StringBuilder assignments = new StringBuilder();
                for (int i = 1; i < names.length; i++) {
                    if (i > 1) { assignments.append(", "); }
                    assignments.append(names[i]).append(" = ?").append(i + 1);
                }
                sql = "UPDATE " + table + " SET " + assignments + " WHERE ID = ?1";
            }
            Query query = em.createNativeQuery(sql);
            for (int i = 0; i < values.length; i++) { query.setParameter(i + 1, values[i]); }
            query.executeUpdate();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) { tx.rollback(); }
            throw ex;
        } finally { em.close(); }
    }

    private static void required(String value, int limit, String label) {
        if (value == null || value.trim().isEmpty() || value.length() > limit) {
            throw new IllegalArgumentException(label + " is required and must contain at most " + limit + " characters.");
        }
    }
}
