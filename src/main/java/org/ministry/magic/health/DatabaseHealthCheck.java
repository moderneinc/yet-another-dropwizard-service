package org.ministry.magic.health;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.db.ManagedDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseHealthCheck extends HealthCheck {

    private final ManagedDataSource dataSource;

    public DatabaseHealthCheck(ManagedDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected Result check() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            if (rs.next()) {
                return Result.healthy("Registry database is operational");
            }
            return Result.unhealthy("Registry database returned no result");
        }
    }
}
