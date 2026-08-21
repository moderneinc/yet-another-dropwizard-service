package org.ministry.magic;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.forms.MultiPartBundle;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import jakarta.servlet.DispatcherType;
import org.jdbi.v3.core.Jdbi;
import org.ministry.magic.bundle.LoggingConfigurationFactoryFactory;
import org.ministry.magic.db.WizardDAO;
import org.ministry.magic.filter.AuditLogFilter;
import org.ministry.magic.health.DatabaseHealthCheck;
import org.ministry.magic.health.WizardRegistryHealthCheck;
import org.ministry.magic.managed.WizardRegistryManaged;
import org.ministry.magic.resources.WizardImportResource;
import org.ministry.magic.resources.WizardReportResource;
import org.ministry.magic.resources.WizardResource;
import org.ministry.magic.service.WizardService;
import org.ministry.magic.servlet.MinistryStatusServlet;

import java.util.EnumSet;

public class WizardRegistryApplication extends Application<WizardRegistryConfiguration> {

    void main(String[] args) throws Exception {
        new WizardRegistryApplication().run(args);
    }

    @Override
    public String getName() {
        return "wizard-registry";
    }

    @Override
    public void initialize(Bootstrap<WizardRegistryConfiguration> bootstrap) {
        // Custom configuration factory for audit logging
        bootstrap.setConfigurationFactoryFactory(new LoggingConfigurationFactoryFactory<>());

        // MultiPart support for file uploads
        bootstrap.addBundle(new MultiPartBundle());

        // Swagger API documentation
        bootstrap.addBundle(new SwaggerBundle<WizardRegistryConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(
                    WizardRegistryConfiguration configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });

        // Register Java Time module for LocalDate/Instant serialization
        bootstrap.getObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public void run(WizardRegistryConfiguration configuration, Environment environment) throws Exception {
        // Build managed data source from configuration
        final DataSourceFactory dataSourceFactory = configuration.getDataSourceFactory();
        dataSourceFactory.setAutoCommitByDefault(false);
        final ManagedDataSource dataSource = dataSourceFactory.build(environment.metrics(), "wizard-db");

        // Register data source with lifecycle management
        environment.lifecycle().manage(dataSource);

        // Set up JDBI and DAO
        final Jdbi jdbi = Jdbi.create(dataSource);
        final WizardDAO dao = new WizardDAO(jdbi);

        // Service layer
        final WizardService wizardService = new WizardService(dao);

        // Managed component — schema creation and data seeding
        environment.lifecycle().manage(new WizardRegistryManaged(dao));

        // Register JAX-RS resources
        environment.jersey().register(new WizardResource(wizardService));
        environment.jersey().register(new WizardImportResource(wizardService));
        environment.jersey().register(new WizardReportResource());

        // Health checks
        environment.healthChecks().register("database", new DatabaseHealthCheck(dataSource));
        environment.healthChecks().register("wizard-registry", new WizardRegistryHealthCheck(wizardService));

        // Make wizard service available via application context for servlet access
        environment.getApplicationContext().setAttribute("wizardService", wizardService);

        // Admin environment — enable admin servlet features
        environment.admin();

        // Servlet filter — audit logging on all API calls
        environment.servlets()
                .addFilter("AuditLogFilter", AuditLogFilter.class)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/api/*");

        // Servlet — Ministry status page
        environment.servlets()
                .addServlet("MinistryStatus", new MinistryStatusServlet(wizardService))
                .addMapping("/ministry/status");
    }
}
