/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.perfect.blog.utils;

import sun.misc.BASE64Encoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class PasswordUtils {

    private PasswordUtils() {}

    private static String makePasswordHash(final String password, final String salt) {
        try {
            final String saltedAndHashed = password + "," + salt;
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(saltedAndHashed.getBytes());
            final BASE64Encoder encoder = new BASE64Encoder();
            final byte[] hashedBytes = (new String(digest.digest(), StandardCharsets.UTF_8)).getBytes();
            return encoder.encode(hashedBytes) + "," + salt;
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
        final BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(randomBytes);
    }
}
