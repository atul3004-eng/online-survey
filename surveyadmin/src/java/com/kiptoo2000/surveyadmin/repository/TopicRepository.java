package com.kiptoo2000.surveyadmin.repository;

import com.kiptoo2000.surveyadmin.model.Topic;
import com.kiptoo2000.surveyadmin.persistence.JpaUtil;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class TopicRepository {

    public List<Topic> findAll() {
        EntityManager entityManager = JpaUtil.createEntityManager();
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
        EntityManager entityManager = JpaUtil.createEntityManager();
        try {
            return entityManager.find(Topic.class, id);
        } finally {
            entityManager.close();
        }
    }

    public Topic save(Topic topic) {
        EntityManager entityManager = JpaUtil.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Topic managedTopic;
            if (topic.getId() == null) {
                entityManager.persist(topic);
                managedTopic = topic;
            } else {
                managedTopic = entityManager.merge(topic);
            }
            transaction.commit();
            return managedTopic;
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
            Topic topic = entityManager.find(Topic.class, id);
            if (topic != null) {
                entityManager.remove(topic);
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
