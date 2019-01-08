package com.mfvanek.perfect.blog.interfaces;

import com.mfvanek.perfect.blog.entities.User;

public interface UserDao {

    /**
     * Validates that username is unique and insert into db
     * @param username given name
     * @param password given password
     * @param email given email
     * @return true if user successfully saved
     */
    boolean addUser(final String username, final String password, final String email);

    User validateLogin(final String username, final String password);

    User findById(final String username);

    boolean exists(final String username);
}
