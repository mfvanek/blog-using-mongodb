package com.mfvanek.perfect.blog.controllers;

import com.mfvanek.perfect.blog.dao.Context;
import com.mfvanek.perfect.blog.routes.RoutesFactory;
import com.mfvanek.perfect.blog.utils.ConnectionUtils;
import com.mongodb.client.MongoDatabase;
import spark.Spark;

public class BlogController {

    private final Context context;

    public BlogController(final String host) {
        final MongoDatabase blogDatabase = ConnectionUtils.getDatabase(host);
        this.context = new Context(blogDatabase);
        Spark.port(8082);
        initializeRoutes();
    }

    private void initializeRoutes() {
        final RoutesFactory routes = RoutesFactory.newInstance(context);

        Spark.get("/", routes.home());
        Spark.get("/post/:permalink", routes.permalink());
        Spark.get("/signup", routes.signup());
        Spark.get("/newpost", routes.newPost());
        Spark.get("/welcome", routes.welcome());
        Spark.get("/login", routes.login());
        Spark.get("/tag/:thetag", routes.tag());
        Spark.get("/post_not_found", routes.postNotFound());
        Spark.get("/logout", routes.logout());
        Spark.get("/internal_error", routes.internalErrors());

        Spark.post("/signup", routes.doSignup());
        Spark.post("/newpost", routes.doNewPost());
        Spark.post("/newcomment", routes.doNewComment());
        Spark.post("/login", routes.doLogin());
        Spark.post("/like", routes.doLike());
    }
}
