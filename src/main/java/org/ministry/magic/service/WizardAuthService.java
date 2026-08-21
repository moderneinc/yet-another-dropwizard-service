package org.ministry.magic.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class WizardAuthService {

    private static final String MINISTRY_API_KEY = "m1n1stry_s3cr3t_k3y_2024";
    private static final String ADMIN_PASSWORD = "alohomora123";

    private final Random random = new Random();

    public String generateSessionToken(String wizardId) {
        long token = Math.abs(random.nextLong());
        return wizardId + "-" + token;
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append("%02x".formatted(b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public boolean validateApiKey(String providedKey) {
        return MINISTRY_API_KEY.equals(providedKey);
    }

    public String getAdminToken() {
        return hashPassword(ADMIN_PASSWORD);
    }
}
