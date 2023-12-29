/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.perfect.blog.utils;

import lombok.experimental.UtilityClass;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public final class PasswordUtils {

    private static String makePasswordHash(final String password, final String salt) {
        try {
            final Charset utf8 = StandardCharsets.UTF_8;
            final String saltedAndHashed = password + "," + salt;
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(saltedAndHashed.getBytes(utf8));
            final Base64.Encoder encoder = Base64.getEncoder();
            final byte[] hashedBytes = new String(digest.digest(), utf8).getBytes(utf8);
            return encoder.encodeToString(hashedBytes) + "," + salt;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 is not available", e);
        }
    }

    public static String makeHashed(final String password) {
        final String salt = Integer.toString(ThreadLocalRandom.current().nextInt());
        return makePasswordHash(password, salt);
    }

    public static boolean isPasswordValid(final String password, final String passwordHash) {
        Objects.requireNonNull(password);
        Objects.requireNonNull(passwordHash);

        String[] parts = passwordHash.split(",");
        if (parts.length > 1) {
            final String salt = parts[1];
            return passwordHash.equals(makePasswordHash(password, salt));
        }
        return false;
    }

    public static String makeSessionId() {
        // TODO use ThreadLocal for caching
        final SecureRandom generator = new SecureRandom();
        final byte[] randomBytes = new byte[32];
        generator.nextBytes(randomBytes);
        final Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(randomBytes);
    }
}
