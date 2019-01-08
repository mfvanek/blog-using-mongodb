/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.perfect.blog.dao;

import com.mfvanek.perfect.blog.entities.User;
import com.mfvanek.perfect.blog.interfaces.UserDao;
import com.mfvanek.perfect.blog.utils.PasswordUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.apache.commons.lang3.StringUtils;

public class UserDaoImpl implements UserDao {

    private final MongoCollection<User> usersCollection;

    UserDaoImpl(final MongoDatabase blogDatabase) {
        this(blogDatabase.getCollection("users", User.class));
    }

    UserDaoImpl(final MongoCollection<User> usersCollection) {
        this.usersCollection = usersCollection;
    }

    @Override
    public boolean addUser(final String username, final String password, final String email) {
        boolean result = false;
        if (!exists(username)) {
            final String passwordHash = PasswordUtils.makeHashed(password);
            final User newUser = new User(username, passwordHash, StringUtils.isNotBlank(email) ? email : null);
            usersCollection.insertOne(newUser);
            result = true;
        }
        return result;
    }

    @Override
    public User validateLogin(final String username, final String password) {
        final User user = findById(username);
        if (user != null) {
            if (PasswordUtils.isPasswordValid(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User findById(final String username) {
        return usersCollection.find(Filters.eq("_id", username)).first();
    }

    @Override
    public boolean exists(final String username) {
        return usersCollection
                .find(Filters.eq("_id", username))
                .projection(Projections.include("_id"))
                .limit(1)
                .first() != null;
    }
}
