/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.perfect.blog.dao;

import com.mfvanek.perfect.blog.entities.Session;
import com.mfvanek.perfect.blog.interfaces.SessionDao;
import com.mongodb.client.MongoCollection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class SessionDaoImplTest {

    @Mock
    private MongoCollection<Session> sessionsCollection;

    @Test
    void findUserNameBySessionId() {
        final String sessionId = "id";
        final String username = "test";
        final SessionDao sessionDao = Mockito.spy(new SessionDaoImpl(sessionsCollection));
        Mockito.doReturn(new Session(sessionId, username)).when(sessionDao).findById(sessionId);
        assertEquals(username, sessionDao.findUserNameBySessionId(sessionId));

        Mockito.doReturn(null).when(sessionDao).findById("another");
        assertNull(sessionDao.findUserNameBySessionId("another"));
    }
}