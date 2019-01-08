/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.perfect.blog.routes;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public abstract class FreemarkerBasedRoute implements Route {

    protected final Template template;

    protected FreemarkerBasedRoute(final String templateName, final Configuration cfg) {
        try {
            template = cfg.getTemplate(templateName);
        } catch (IOException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public Object handle(Request request, Response response) {
        final StringWriter writer = new StringWriter();
        try {
            doHandle(request, response, writer);
        } catch (Exception e) {
            e.printStackTrace();
            response.redirect("/internal_error");
        }
        return writer;
    }

    protected abstract void doHandle(final Request request, final Response response, final Writer writer);

    protected void processTemplate(final SimpleHash root, final Writer writer) {
        try {
            template.process(root, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    void processTemplate(final Map<String, String> root, final Writer writer) {
        try {
            template.process(root, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }
}
