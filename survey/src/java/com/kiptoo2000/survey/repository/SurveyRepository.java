package com.kiptoo2000.survey.repository;

import com.kiptoo2000.survey.model.AnswerDetail;
import com.kiptoo2000.survey.model.AnswerDetailId;
import com.kiptoo2000.survey.model.AnswerMaster;
import com.kiptoo2000.survey.model.Question;
import com.kiptoo2000.survey.model.Topic;
import com.kiptoo2000.survey.persistence.JpaUtility;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class SurveyRepository  implements Serializable {

    public boolean storeSurveyResults(Long topicId, List<Question> questions) {
        EntityManager entityManager = JpaUtility.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Topic topic = entityManager.find(Topic.class, topicId);
            AnswerMaster survey = new AnswerMaster();
            survey.setTopic(topic);
            entityManager.persist(survey);
            entityManager.flush();

            for (Question question : questions) {
                Question managedQuestion = entityManager.find(Question.class, question.getId());
                AnswerDetail detail = new AnswerDetail();
                AnswerDetailId detailId = new AnswerDetailId();
                detailId.setSurveyId(survey.getSurveyId());
                detailId.setQuestionId(managedQuestion.getId());
                detail.setId(detailId);
                detail.setSurvey(survey);
                detail.setQuestion(managedQuestion);
                detail.setAnswer(question.getAnswer());
                entityManager.persist(detail);
            }

            transaction.commit();
            return true;
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            entityManager.close();
        }
    }
}
