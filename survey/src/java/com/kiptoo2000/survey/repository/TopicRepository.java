package com.kiptoo2000.survey.repository;

import com.kiptoo2000.survey.model.Question;
import com.kiptoo2000.survey.model.Topic;
import com.kiptoo2000.survey.persistence.JpaUtility;
import java.util.List;
import javax.persistence.EntityManager;

public class TopicRepository {

    public List<Topic> findAll() {
        EntityManager entityManager = JpaUtility.createEntityManager();
        try {
            return entityManager.createQuery(
                    "SELECT t FROM Topic t ORDER BY t.title", Topic.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public Topic findById(Long id) {
        if (id == null) {
            return null;
        }
        EntityManager entityManager = JpaUtility.createEntityManager();
        try {
            return entityManager.find(Topic.class, id);
        } finally {
            entityManager.close();
        }
    }

    public List<Question> findQuestionsByTopicId(Long topicId) {
        EntityManager entityManager = JpaUtility.createEntityManager();
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
}
