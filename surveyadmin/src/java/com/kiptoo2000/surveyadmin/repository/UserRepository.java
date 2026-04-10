package com.kiptoo2000.surveyadmin.repository;

import com.kiptoo2000.surveyadmin.model.User;
import com.kiptoo2000.surveyadmin.persistence.JpaUtil;
import javax.persistence.EntityManager;

public class UserRepository {

    public boolean isValidLogin(String username, String password) {
        EntityManager entityManager = JpaUtil.createEntityManager();
        try {
            User user = entityManager.find(User.class, username);
            return user != null && user.getPassword().equals(password);
        } finally {
            entityManager.close();
        }
    }
}
