/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.perfect.blog;

import com.mfvanek.perfect.blog.controllers.BlogController;

// http://localhost:8082/login
class BlogMain {

    public static void main(String[] args) {
        if (args.length == 0) {
            new BlogController("localhost");
        }
        else {
            new BlogController(args[0]);
        }
    }
}
