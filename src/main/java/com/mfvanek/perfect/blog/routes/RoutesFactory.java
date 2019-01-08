package com.mfvanek.perfect.blog.routes;

import com.mfvanek.perfect.blog.controllers.BlogController;
import com.mfvanek.perfect.blog.dao.Context;
import com.mfvanek.perfect.blog.entities.BlogPost;
import com.mfvanek.perfect.blog.entities.User;
import com.mfvanek.perfect.blog.utils.SessionCookieUtils;
import com.mfvanek.perfect.blog.utils.TagUtils;
import com.mfvanek.perfect.blog.utils.Validator;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import org.apache.commons.text.StringEscapeUtils;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.http.Cookie;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RoutesFactory {

    private final Configuration configuration;
    private final Context context;

    private RoutesFactory(final Context context, final Configuration configuration) {
        this.configuration = configuration;
        this.context = context;
    }

    /**
     * Used to process internal errors
     *
     * @return Route
     */
    public Route internalErrors() {
        return new FreemarkerBasedRoute("error_template.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {
                SimpleHash root = new SimpleHash();
                root.put("error", "System has encountered an error.");
                processTemplate(root, writer);
            }
        };
    }

    /**
     * Allows the user to logout of the blog
     *
     * @return Route
     */
    public Route logout() {
        return new FreemarkerBasedRoute("signup.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {
                final String sessionId = SessionCookieUtils.getSessionCookie(request);

                if (sessionId == null) {
                    // no session to end
                    response.redirect("/login");
                } else {
                    // deletes from session table
                    context.getSessionDao().endSession(sessionId);

                    // this should delete the cookie
                    final Cookie c = SessionCookieUtils.getSessionCookieActual(request);
                    if (c != null) {
                        c.setMaxAge(0);
                        response.raw().addCookie(c);
                    }

                    response.redirect("/login");
                }
            }
        };
    }

    /**
     * Tells the user that the URL is dead
     *
     * @return Route
     */
    public Route postNotFound() {
        return new FreemarkerBasedRoute("post_not_found.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {
                SimpleHash root = new SimpleHash();
                processTemplate(root, writer);
            }
        };
    }

    /**
     * Will allow a user to click Like on a post
     *
     * @return Route
     */
    public Route doLike() {
        return new FreemarkerBasedRoute("entry_template.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {
                final String username = context.getSessionDao().findUserNameBySessionId(SessionCookieUtils.getSessionCookie(request));
                if (username != null) {
                    String permalink = request.queryParams("permalink");
                    BlogPost post = context.getBlogPostDao().findByPermalink(permalink);
                    //  if post not found, redirect to post not found error
                    if (post == null) {
                        response.redirect("/post_not_found");
                    } else {
                        String commentOrdinalStr = request.queryParams("comment_ordinal");
                        // look up the post in question
                        final int ordinal = Integer.parseInt(commentOrdinalStr);

                        context.getBlogPostDao().likePost(permalink, ordinal);
                        response.redirect("/post/" + permalink);
                    }
                } else {
                    response.redirect("/login");
                }
            }
        };
    }

    /**
     * Show the posts filed under a certain tag
     *
     * @return Route
     */
    public Route tag() {
        return new FreemarkerBasedRoute("blog_template.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {

                String username = context.getSessionDao().findUserNameBySessionId(SessionCookieUtils.getSessionCookie(request));
                SimpleHash root = new SimpleHash();

                String tag = StringEscapeUtils.escapeHtml4(request.params(":thetag"));
                final List<BlogPost> posts = context.getBlogPostDao().findByTagDateDescending(tag);
                root.put("myposts", posts);
                if (username != null) {
                    root.put("username", username);
                }
                processTemplate(root, writer);
            }
        };
    }

    /**
     * Present the login page
     *
     * @return Route
     */
    public Route login() {
        return new FreemarkerBasedRoute("login.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {
                SimpleHash root = new SimpleHash();
                root.put("username", "");
                root.put("login_error", "");
                processTemplate(root, writer);
            }
        };
    }

    /**
     * Process output coming from login form. On success redirect folks to the welcome page
     * on failure, just return an error and let them try again.
     *
     * @return Route
     */
    public Route doLogin() {
        return new FreemarkerBasedRoute("login.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {
                String username = request.queryParams("username");
                String password = request.queryParams("password");

                System.out.println("Login: User submitted: " + username + "  " + password);

                final User user = context.getUserDao().validateLogin(username, password);
                if (user != null) {
                    // valid user, let's log them in
                    String sessionID = context.getSessionDao().startSession(user.getId());

                    if (sessionID == null) {
                        response.redirect("/internal_error");
                    } else {
                        // set the cookie for the user's browser
                        response.raw().addCookie(new Cookie("session", sessionID));

                        response.redirect("/welcome");
                    }
                } else {
                    SimpleHash root = new SimpleHash();
                    root.put("username", StringEscapeUtils.escapeHtml4(username));
                    root.put("password", "");
                    root.put("login_error", "Invalid Login");
                    processTemplate(root, writer);
                }
            }
        };
    }

    /**
     * Process a new comment
     *
     * @return Route
     */
    public Route doNewComment() {
        return new FreemarkerBasedRoute("entry_template.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {
                String name = StringEscapeUtils.escapeHtml4(request.queryParams("commentName"));
                String email = StringEscapeUtils.escapeHtml4(request.queryParams("commentEmail"));
                String body = StringEscapeUtils.escapeHtml4(request.queryParams("commentBody"));
                String permalink = request.queryParams("permalink");

                BlogPost post = context.getBlogPostDao().findByPermalink(permalink);
                if (post == null) {
                    response.redirect("/post_not_found");
                }
                // check that comment is good
                else if (name.equals("") || body.equals("")) {
                    // bounce this back to the user for correction
                    SimpleHash root = new SimpleHash();
                    SimpleHash comment = new SimpleHash();

                    comment.put("name", name);
                    comment.put("email", email);
                    comment.put("body", body);
                    root.put("comment", comment);
                    root.put("post", post);
                    root.put("errors", "Post must contain your name and an actual comment");
                    processTemplate(root, writer);
                } else {
                    context.getBlogPostDao().addPostComment(name, email, body, permalink);
                    response.redirect("/post/" + permalink);
                }
            }
        };
    }

    public Route welcome() {
        return new FreemarkerBasedRoute("welcome.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {
                final String cookie = SessionCookieUtils.getSessionCookie(request);
                final String username = context.getSessionDao().findUserNameBySessionId(cookie);
                if (username == null) {
                    System.out.println("welcome() can't identify the user, redirecting to signup");
                    response.redirect("/signup");
                } else {
                    SimpleHash root = new SimpleHash();
                    root.put("username", username);
                    processTemplate(root, writer);
                }
            }
        };
    }

    /**
     * Handle the new post submission
     *
     * @return Route
     */
    public Route doNewPost() {
        return new FreemarkerBasedRoute("newpost_template.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {

                String title = StringEscapeUtils.escapeHtml4(request.queryParams("subject"));
                String post = StringEscapeUtils.escapeHtml4(request.queryParams("body"));
                String tags = StringEscapeUtils.escapeHtml4(request.queryParams("tags"));

                String username = context.getSessionDao().findUserNameBySessionId(SessionCookieUtils.getSessionCookie(request));

                if (username == null) {
                    response.redirect("/login");    // only logged in users can post to blog
                } else if (title.equals("") || post.equals("")) {
                    // redisplay page with errors
                    Map<String, String> root = new HashMap<>();
                    root.put("errors", "post must contain a title and blog entry.");
                    root.put("subject", title);
                    root.put("username", username);
                    root.put("tags", tags);
                    root.put("body", post);
                    processTemplate(root, writer);
                } else {
                    // extract tags
                    List<String> tagsArray = TagUtils.extractTags(tags);

                    // substitute some <p> for the paragraph breaks
                    post = post.replaceAll("\\r?\\n", "<p>");

                    String permalink = context.getBlogPostDao().addPost(title, post, tagsArray, username);

                    // now redirect to the blog permalink
                    response.redirect("/post/" + permalink);
                }
            }
        };
    }

    /**
     * Will present the form used to process new blog posts
     *
     * @return Route
     */
    public Route newPost() {
        return new FreemarkerBasedRoute("newpost_template.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {

                // get cookie
                String username = context.getSessionDao().findUserNameBySessionId(SessionCookieUtils.getSessionCookie(request));

                if (username == null) {
                    // looks like a bad request. user is not logged in
                    response.redirect("/login");
                } else {
                    SimpleHash root = new SimpleHash();
                    root.put("username", username);
                    processTemplate(root, writer);
                }
            }
        };
    }

    /**
     * Present signup form for blog
     *
     * @return Route
     */
    public Route signup() {
        return new FreemarkerBasedRoute("signup.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {

                SimpleHash root = new SimpleHash();
                // initialize values for the form.
                root.put("username", "");
                root.put("password", "");
                root.put("email", "");
                root.put("password_error", "");
                root.put("username_error", "");
                root.put("email_error", "");
                root.put("verify_error", "");
                processTemplate(root, writer);
            }
        };
    }

    /**
     * Handle the signup post
     *
     * @return Route
     */
    public Route doSignup() {
        return new FreemarkerBasedRoute("signup.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {
                String email = request.queryParams("email");
                String username = request.queryParams("username");
                String password = request.queryParams("password");
                String verify = request.queryParams("verify");

                Map<String, String> root = new HashMap<>();
                root.put("username", StringEscapeUtils.escapeHtml4(username));
                root.put("email", StringEscapeUtils.escapeHtml4(email));

                if (Validator.validateSignup(username, password, verify, email, root)) {
                    // good user
                    System.out.println("Signup: Creating user with: " + username + " " + password);
                    if (!context.getUserDao().addUser(username, password, email)) {
                        // duplicate user
                        root.put("username_error", "Username already in use, Please choose another");
                        processTemplate(root, writer);
                    } else {
                        // good user, let's start a session
                        String sessionID = context.getSessionDao().startSession(username);
                        System.out.println("Session ID is" + sessionID);

                        response.raw().addCookie(new Cookie("session", sessionID));
                        response.redirect("/welcome");
                    }
                } else {
                    // bad signup
                    System.out.println("User Registration did not validate");
                    processTemplate(root, writer);
                }
            }
        };
    }

    /**
     * Used to display actual blog post detail page
     *
     * @return Route
     */
    public Route permalink() {
        return new FreemarkerBasedRoute("entry_template.ftl", configuration) {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) {
                String permalink = request.params(":permalink");

                System.out.println("/post: get " + permalink);

                BlogPost post = context.getBlogPostDao().findByPermalink(permalink);
                if (post == null) {
                    response.redirect("/post_not_found");
                } else {
                    // empty comment to hold new comment in form at bottom of blog entry detail page
                    SimpleHash newComment = new SimpleHash();
                    newComment.put("name", "");
                    newComment.put("email", "");
                    newComment.put("body", "");

                    SimpleHash root = new SimpleHash();
                    root.put("post", post);
                    root.put("comment", newComment);
                    processTemplate(root, writer);
                }
            }
        };
    }

    /**
     * This is the blog home page
     *
     * @return Route
     */
    public Route home() {
        return new FreemarkerBasedRoute("blog_template.ftl", configuration) {
            @Override
            public void doHandle(Request request, Response response, Writer writer) {
                final String username = context.getSessionDao().findUserNameBySessionId(SessionCookieUtils.getSessionCookie(request));
                final List<BlogPost> posts = context.getBlogPostDao().findByDateDescending(10);
                SimpleHash root = new SimpleHash();
                root.put("myposts", posts);
                if (username != null) {
                    root.put("username", username);
                }
                processTemplate(root, writer);
            }
        };
    }

    private static Configuration createFreemarkerConfiguration() {
        Configuration configuration = new Configuration();
        // TODO
        configuration.setClassForTemplateLoading(BlogController.class, "/freemarker");
        return configuration;
    }

    public static RoutesFactory newInstance(final Context context) {
        return new RoutesFactory(context, createFreemarkerConfiguration());
    }
}
