package org.ministry.magic.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WizardAuthServiceTest {

    @Test
    void hashPasswordUsesSha256() {
        WizardAuthService service = new WizardAuthService("api-key", "admin-password");

        String hash = service.hashPassword("alohomora");

        assertThat(hash).isEqualTo("f6cf7f88816a2ec9b06d512d87224c0a7e04f4bc9b0acefbe837af735b4c78e9");
    }

    @Test
    void validateApiKeyUsesConfiguredValue() {
        WizardAuthService service = new WizardAuthService("api-key", "admin-password");

        assertThat(service.validateApiKey("api-key")).isTrue();
        assertThat(service.validateApiKey("wrong-key")).isFalse();
    }

    @Test
    void validateApiKeyReturnsFalseWhenApiKeyNotConfigured() {
        WizardAuthService service = new WizardAuthService(null, "admin-password");

        assertThat(service.validateApiKey("api-key")).isFalse();
    }
}
