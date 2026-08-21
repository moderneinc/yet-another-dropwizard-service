package org.ministry.magic.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class WizardAuthService {

    private static final String API_KEY_PROPERTY = "ministry.api.key";
    private static final String ADMIN_PASSWORD_PROPERTY = "ministry.admin.password";

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateSessionToken(String wizardId) {
        byte[] tokenBytes = new byte[24];
        secureRandom.nextBytes(tokenBytes);
        return wizardId + "-" + Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
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
        if (providedKey == null) {
            return false;
        }
        String configuredKey = System.getProperty(API_KEY_PROPERTY, "");
        return !configuredKey.isEmpty() && MessageDigest.isEqual(
                configuredKey.getBytes(StandardCharsets.UTF_8),
                providedKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getAdminToken() {
        return hashPassword(System.getProperty(ADMIN_PASSWORD_PROPERTY, ""));
    }
}
