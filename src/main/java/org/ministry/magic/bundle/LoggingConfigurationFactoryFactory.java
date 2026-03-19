package org.ministry.magic.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingConfigurationFactoryFactory<T> implements ConfigurationFactoryFactory<T> {

    private static final Logger log = LoggerFactory.getLogger(LoggingConfigurationFactoryFactory.class);

    private final DefaultConfigurationFactoryFactory<T> delegate = new DefaultConfigurationFactoryFactory<>();

    @Override
    public ConfigurationFactory<T> create(Class<T> klass, Validator validator,
                                           ObjectMapper objectMapper, String propertyPrefix) {
        log.info("Ministry Configuration: parsing configuration for {}", klass.getSimpleName());
        return delegate.create(klass, validator, objectMapper, propertyPrefix);
    }
}
