package com.mfvanek.perfect.blog.utils;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SimpleHashHelper {

    public static SimpleHash createHash() {
        return new SimpleHash((ObjectWrapper) null);
    }
}
