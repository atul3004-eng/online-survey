package com.kiptoo2000.surveyadmin.repository;

import com.kiptoo2000.surveyadmin.model.User;
import com.kiptoo2000.surveyadmin.persistence.JpaUtil;
import javax.persistence.EntityManager;

public class UserRepository {

    public User findByUsername(String username) {
        EntityManager entityManager = JpaUtil.createEntityManager();
        try {
            return entityManager.find(User.class, username);
        } finally {
            entityManager.close();
        }
    }

}
