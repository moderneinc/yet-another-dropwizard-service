package org.ministry.magic.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class WizardAuthService {

    private final String ministryApiKey;
    private final String adminPassword;

    private final SecureRandom random = new SecureRandom();

    public WizardAuthService(String ministryApiKey, String adminPassword) {
        this.ministryApiKey = ministryApiKey;
        this.adminPassword = adminPassword;
    }

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
        return ministryApiKey.equals(providedKey);
    }

    public String getAdminToken() {
        return hashPassword(adminPassword);
    }
}
