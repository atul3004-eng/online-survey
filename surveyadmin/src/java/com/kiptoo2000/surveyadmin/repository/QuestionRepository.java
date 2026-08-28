package com.kiptoo2000.surveyadmin.repository;

import com.kiptoo2000.surveyadmin.model.Question;
import com.kiptoo2000.surveyadmin.model.Topic;
import com.kiptoo2000.surveyadmin.persistence.JpaUtil;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class QuestionRepository {

    public List<Question> findByTopicId(Long topicId) {
        EntityManager entityManager = JpaUtil.createEntityManager();
        try {
            return entityManager.createQuery(
                    "SELECT q FROM Question q WHERE q.topic.id = :topicId ORDER BY q.id",
                    Question.class)
                    .setParameter("topicId", topicId)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public Question findById(Long id) {
        if (id == null) {
            return null;
        }
        EntityManager entityManager = JpaUtil.createEntityManager();
        try {
            return entityManager.find(Question.class, id);
        } finally {
            entityManager.close();
        }
    }

    public Question save(Question question, Long topicId) {
        EntityManager entityManager = JpaUtil.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Topic topic = entityManager.find(Topic.class, topicId);
            question.setTopic(topic);
            Question managedQuestion;
            if (question.getId() == null) {
                entityManager.persist(question);
                managedQuestion = question;
            } else {
                managedQuestion = entityManager.merge(question);
            }
            transaction.commit();
            return managedQuestion;
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    public void delete(Long id) {
        EntityManager entityManager = JpaUtil.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Question question = entityManager.find(Question.class, id);
            if (question != null) {
                entityManager.remove(question);
            }
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }
}
