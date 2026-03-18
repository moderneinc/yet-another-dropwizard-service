package org.ministry.magic.health;

import com.codahale.metrics.health.HealthCheck;
import org.ministry.magic.service.WizardService;

public class WizardRegistryHealthCheck extends HealthCheck {

    private final WizardService wizardService;

    public WizardRegistryHealthCheck(WizardService wizardService) {
        this.wizardService = wizardService;
    }

    @Override
    protected Result check() {
        long activeCount = wizardService.countActiveWizards();
        if (activeCount > 0) {
            return Result.healthy("Registry contains %d active wizard(s)", activeCount);
        }
        return Result.unhealthy("Registry contains no active wizards — possible data issue");
    }
}
