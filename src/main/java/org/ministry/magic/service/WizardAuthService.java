package org.ministry.magic.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class WizardAuthService {

    private final SecureRandom random = new SecureRandom();

    public String generateSessionToken(String wizardId) {
        long token = Math.abs(random.nextLong());
        return wizardId + "-" + token;
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public boolean validateApiKey(String providedKey) {
        String apiKey = System.getenv("MINISTRY_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("MINISTRY_API_KEY environment variable is not configured");
        }
        return apiKey.equals(providedKey);
    }

    public String getAdminToken() {
        String adminPassword = System.getenv("MINISTRY_ADMIN_PASSWORD");
        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException("MINISTRY_ADMIN_PASSWORD environment variable is not configured");
        }
        return hashPassword(adminPassword);
    }
}
