package org.ministry.magic.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WizardAuthServiceTest {

    @AfterEach
    void clearProperties() {
        System.clearProperty("ministry.api.key");
        System.clearProperty("ministry.admin.password");
    }

    @Test
    void validatesConfiguredApiKey() {
        System.setProperty("ministry.api.key", "secret-key");
        WizardAuthService service = new WizardAuthService();

        assertThat(service.validateApiKey("secret-key")).isTrue();
        assertThat(service.validateApiKey("wrong-key")).isFalse();
    }

    @Test
    void hashesWithSha256() {
        WizardAuthService service = new WizardAuthService();

        String hash = service.hashPassword("alohomora");

        assertThat(hash).hasSize(64);
    }

    @Test
    void generatesNonEmptyToken() {
        WizardAuthService service = new WizardAuthService();

        String token = service.generateSessionToken("wizard-123");

        assertThat(token).startsWith("wizard-123-");
        assertThat(token.length()).isGreaterThan("wizard-123-".length());
    }
}
