/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.perfect.blog.dao;

import com.mfvanek.perfect.blog.interfaces.BlogPostDao;
import com.mfvanek.perfect.blog.interfaces.SessionDao;
import com.mfvanek.perfect.blog.interfaces.UserDao;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

@Getter
public class Context {

    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final BlogPostDao blogPostDao;

    public Context(final MongoDatabase blogDatabase) {
        this.blogPostDao = new BlogPostDaoImpl(blogDatabase);
        this.userDao = new UserDaoImpl(blogDatabase);
        this.sessionDao = new SessionDaoImpl(blogDatabase);
    }
}
