package org.ministry.magic;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.ResourceHelpers;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class WizardRegistryConfigurationTest {

    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void canDeserializeFromYaml() throws Exception {
        ConfigurationFactoryFactory<WizardRegistryConfiguration> factoryFactory =
                new DefaultConfigurationFactoryFactory<>();

        ConfigurationFactory<WizardRegistryConfiguration> factory =
                factoryFactory.create(WizardRegistryConfiguration.class, validator, objectMapper, "dw");

        File configFile = new File(ResourceHelpers.resourceFilePath("test-wizard-registry.yml"));
        WizardRegistryConfiguration config = factory.build(configFile);

        assertThat(config).isNotNull();
        assertThat(config.getDataSourceFactory()).isNotNull();
        assertThat(config.getDataSourceFactory().getDriverClass()).isEqualTo("org.h2.Driver");
        assertThat(config.getDataSourceFactory().getUrl()).contains("h2:mem");
        assertThat(config.getDataSourceFactory().getUser()).isEqualTo("sa");
    }

    @Test
    void dataSourceFactoryHasDefaults() {
        WizardRegistryConfiguration config = new WizardRegistryConfiguration();
        assertThat(config.getDataSourceFactory()).isNotNull();
    }

    @Test
    void jacksonObjectMapperCreation() {
        ObjectMapper mapper = Jackson.newObjectMapper();
        assertThat(mapper).isNotNull();
    }
}
