/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.perfect.blog.dao;

import com.mfvanek.perfect.blog.entities.Session;
import com.mfvanek.perfect.blog.interfaces.SessionDao;
import com.mfvanek.perfect.blog.utils.PasswordUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

import java.util.Objects;

public class SessionDaoImpl implements SessionDao {

    private final MongoCollection<Session> sessionsCollection;

    SessionDaoImpl(final MongoDatabase blogDatabase) {
        this(blogDatabase.getCollection("sessions", Session.class));
    }

    SessionDaoImpl(final MongoCollection<Session> sessionsCollection) {
        this.sessionsCollection = sessionsCollection;
    }

    @Override
    public String findUserNameBySessionId(final String sessionId) {
        final Session session = findById(sessionId);
        if (session != null) {
            return session.getUserName();
        }
        return null;
    }

    @Override
    public String startSession(final String username) {
        Objects.requireNonNull(username);
        final String sessionId = PasswordUtils.makeSessionId();
        final Session session = new Session(sessionId, username);
        sessionsCollection.insertOne(session);
        return session.getId();
    }

    @Override
    public boolean endSession(final String sessionId) {
        Objects.requireNonNull(sessionId);
        DeleteResult deleteResult = sessionsCollection.deleteOne(Filters.eq("_id", sessionId));
        return deleteResult.getDeletedCount() > 0;
    }

    @Override
    public Session findById(final String sessionId) {
        Objects.requireNonNull(sessionId);
        return sessionsCollection.find(Filters.eq("_id", sessionId)).first();
    }
}
