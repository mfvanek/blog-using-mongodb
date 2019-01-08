package com.mfvanek.perfect.blog.interfaces;

import com.mfvanek.perfect.blog.entities.Session;

public interface SessionDao {

    String findUserNameBySessionId(final String sessionId);

    String startSession(final String username);

    /**
     * Ends the session by deleting it from the sessions collection
     * @param sessionId given session
     * @return true if session was deleted
     */
    boolean endSession(final String sessionId);

    Session findById(final String sessionId);
}
