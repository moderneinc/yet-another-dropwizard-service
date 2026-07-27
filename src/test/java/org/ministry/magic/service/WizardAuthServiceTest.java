package org.ministry.magic.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WizardAuthServiceTest {

    private final WizardAuthService authService = new WizardAuthService();

    @Test
    void hashPasswordUsesSha256HexLength() {
        String hash = authService.hashPassword("alohomora123");

        assertThat(hash).hasSize(64);
        assertThat(hash).matches("[0-9a-f]+$");
    }

    @Test
    void generateSessionTokenUsesUnsignedNumericSuffix() {
        String token = authService.generateSessionToken("wizard-1");

        assertThat(token).startsWith("wizard-1-");
        assertThat(token.substring("wizard-1-".length())).matches("[0-9]+$");
    }
}
