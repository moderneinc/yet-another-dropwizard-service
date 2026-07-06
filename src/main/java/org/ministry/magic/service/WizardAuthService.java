package org.ministry.magic.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

public class WizardAuthService {

    private static final String MINISTRY_API_KEY_PROPERTY = "ministry.api.key";
    private static final String ADMIN_PASSWORD_PROPERTY = "ministry.admin.password";

    private final SecureRandom random = new SecureRandom();
    private final String ministryApiKey;
    private final String adminPassword;

    public WizardAuthService() {
        this(System.getProperty(MINISTRY_API_KEY_PROPERTY), System.getProperty(ADMIN_PASSWORD_PROPERTY));
    }

    WizardAuthService(String ministryApiKey, String adminPassword) {
        this.ministryApiKey = Objects.requireNonNullElse(ministryApiKey, "");
        this.adminPassword = Objects.requireNonNullElse(adminPassword, "");
    }

    public String generateSessionToken(String wizardId) {
        long token = Math.abs(random.nextLong());
        return wizardId + "-" + token;
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
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
        if (ministryApiKey.isEmpty() || providedKey == null) {
            return false;
        }
        return MessageDigest.isEqual(
                ministryApiKey.getBytes(StandardCharsets.UTF_8),
                providedKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String getAdminToken() {
        return hashPassword(adminPassword);
    }
}
