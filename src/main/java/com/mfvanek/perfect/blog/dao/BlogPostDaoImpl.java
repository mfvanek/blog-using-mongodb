/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.perfect.blog.dao;

import com.mfvanek.perfect.blog.entities.BlogPost;
import com.mfvanek.perfect.blog.entities.Comment;
import com.mfvanek.perfect.blog.interfaces.BlogPostDao;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BlogPostDaoImpl implements BlogPostDao {

    private final MongoCollection<BlogPost> postsCollection;

    BlogPostDaoImpl(final MongoDatabase blogDatabase) {
        this(blogDatabase.getCollection("posts", BlogPost.class));
    }

    BlogPostDaoImpl(final MongoCollection<BlogPost> postsCollection) {
        this.postsCollection = postsCollection;
    }

    @Override
    public BlogPost findByPermalink(final String permalink) {
        Objects.requireNonNull(permalink);
        return postsCollection.find(Filters.eq("permalink", permalink)).first();
    }

    @Override
    public List<BlogPost> findByDateDescending(final int limit) {
        return postsCollection.find()
                .sort(Sorts.descending("date"))
                .limit(limit)
                .into(new ArrayList<>(limit));
    }

    @Override
    public List<BlogPost> findByTagDateDescending(final String tag) {
        Objects.requireNonNull(tag);
        final int limit = 10;
        return postsCollection.find(Filters.eq("tags", tag))
                .sort(Sorts.descending("date"))
                .limit(limit)
                .into(new ArrayList<>(limit));
    }

    @Override
    public String addPost(final String title, final String body, final List<String> tags, final String username) {
        final String permalink = BlogPost.makePermalink(title);
        final BlogPost post = new BlogPost(null, title, username, body, permalink, tags, new ArrayList<>(), new Date());
        postsCollection.insertOne(post);
        return permalink;
    }

    @Override
    public void addPostComment(final String name, final String email, final String body, final String permalink) {
        final Comment comment = new Comment(name, body, StringUtils.isNotBlank(email) ? email : null, 0);
        postsCollection.updateOne(Filters.eq("permalink", permalink),
                new Document("$push", new Document("comments", comment.toDocument())));
    }

    @Override
    public void likePost(final String permalink, final int ordinal) {
        postsCollection.updateOne(Filters.eq("permalink", permalink),
                new Document("$inc", new Document("comments." + ordinal + ".num_likes", 1)));
    }
}
