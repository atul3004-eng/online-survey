package com.kiptoo2000.surveyadmin.repository;

import com.kiptoo2000.surveyadmin.model.Question;
import com.kiptoo2000.surveyadmin.model.SurveyResultRow;
import com.kiptoo2000.surveyadmin.persistence.JpaUtil;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

public class SurveyResultRepository {

    public List<SurveyResultRow> findByTopicId(Long topicId) {
        EntityManager entityManager = JpaUtil.createEntityManager();
        try {
            Number responseCount = (Number) entityManager.createNativeQuery(
                    "select count(*) from answers_master where topicid = ?")
                    .setParameter(1, topicId)
                    .getSingleResult();
            int totalResponses = responseCount != null ? responseCount.intValue() : 0;

            List<Question> questions = entityManager.createQuery(
                    "SELECT q FROM Question q WHERE q.topic.id = :topicId ORDER BY q.id",
                    Question.class)
                    .setParameter("topicId", topicId)
                    .getResultList();

            List<SurveyResultRow> rows = new ArrayList<SurveyResultRow>();
            for (Question question : questions) {
                Object[] answerSummary = (Object[]) entityManager.createNativeQuery(
                        "select " +
                        "sum(case when answer = '1' then 1 else 0 end) as opt1count, " +
                        "sum(case when answer = '2' then 1 else 0 end) as opt2count, " +
                        "sum(case when answer = '3' then 1 else 0 end) as opt3count " +
                        "from answers_details where questionid = ?")
                        .setParameter(1, question.getId())
                        .getSingleResult();

                SurveyResultRow row = new SurveyResultRow();
                row.setQuestion(question.getText());
                row.setOption1(question.getOption1());
                row.setOption2(question.getOption2());
                row.setOption3(question.getOption3());
                row.setOption1Percent(toPercent(answerSummary[0], totalResponses));
                row.setOption2Percent(toPercent(answerSummary[1], totalResponses));
                row.setOption3Percent(toPercent(answerSummary[2], totalResponses));
                rows.add(row);
            }
            return rows;
        } finally {
            entityManager.close();
        }
    }

    private int toPercent(Object countValue, int totalResponses) {
        if (totalResponses == 0 || countValue == null) {
            return 0;
        }
        int count;
        if (countValue instanceof BigInteger) {
            count = ((BigInteger) countValue).intValue();
        } else if (countValue instanceof Number) {
            count = ((Number) countValue).intValue();
        } else {
            count = Integer.parseInt(String.valueOf(countValue));
        }
        return (int) Math.round((count * 100.0d) / totalResponses);
    }
}
