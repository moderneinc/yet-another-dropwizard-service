package org.ministry.magic.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class WizardAuthService {

    private static final String MINISTRY_API_KEY = System.getenv("MINISTRY_API_KEY") != null
            ? System.getenv("MINISTRY_API_KEY") : "CHANGE_ME";
    private static final String ADMIN_PASSWORD = System.getenv("MINISTRY_ADMIN_PASSWORD") != null
            ? System.getenv("MINISTRY_ADMIN_PASSWORD") : "CHANGE_ME";

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
        return MINISTRY_API_KEY.equals(providedKey);
    }

    public String getAdminToken() {
        return hashPassword(ADMIN_PASSWORD);
    }
}
