package com.mfvanek.perfect.blog.dao;

import com.mfvanek.perfect.blog.entities.User;
import com.mfvanek.perfect.blog.interfaces.UserDao;
import com.mongodb.client.MongoCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserDaoImplTest {

    @Mock
    private MongoCollection<User> usersCollection;

    @Test
    void addUser_alreadyExists() {
        final String username = "test";
        final UserDao userDao = Mockito.spy(new UserDaoImpl(usersCollection));
        Mockito.doReturn(true).when(userDao).exists(username);
        Assertions.assertFalse(userDao.addUser(username, "", ""));
    }

    @Test
    void addUser_notExists() {
        final String username = "test";
        final UserDao userDao = Mockito.spy(new UserDaoImpl(usersCollection));
        Mockito.doReturn(false).when(userDao).exists(username);
        assertTrue(userDao.addUser(username, "", ""));
    }
}