/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.perfect.blog;

import com.mfvanek.perfect.blog.controllers.BlogController;

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
