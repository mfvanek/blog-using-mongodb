/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.perfect.blog.interfaces;

import com.mfvanek.perfect.blog.entities.BlogPost;

import java.util.List;

public interface BlogPostDao {

    BlogPost findByPermalink(final String permalink);

    List<BlogPost> findByDateDescending(final int limit);

    List<BlogPost> findByTagDateDescending(final String tag);

    String addPost(final String title, final String body, final List<String> tags, final String username);

    void addPostComment(final String name, final String email, final String body, final String permalink);

    void likePost(final String permalink, final int ordinal);
}
