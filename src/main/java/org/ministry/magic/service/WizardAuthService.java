package org.ministry.magic.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class WizardAuthService {

    private static final String MINISTRY_API_KEY;
    private static final String ADMIN_PASSWORD;

    static {
        String apiKey = System.getenv("MINISTRY_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("MINISTRY_API_KEY environment variable must be set");
        }
        MINISTRY_API_KEY = apiKey;

        String adminPwd = System.getenv("MINISTRY_ADMIN_PASSWORD");
        if (adminPwd == null || adminPwd.isBlank()) {
            throw new IllegalStateException("MINISTRY_ADMIN_PASSWORD environment variable must be set");
        }
        ADMIN_PASSWORD = adminPwd;
    }

    private final SecureRandom random = new SecureRandom();

    public String generateSessionToken(String wizardId) {
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return wizardId + "-" + Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
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
        return MINISTRY_API_KEY.equals(providedKey);
    }

    public String getAdminToken() {
        return hashPassword(ADMIN_PASSWORD);
    }
}
