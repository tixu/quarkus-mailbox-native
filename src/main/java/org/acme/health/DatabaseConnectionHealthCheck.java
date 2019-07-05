package org.acme.health;

import java.sql.Connection;
import java.sql.Statement;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import io.agroal.api.AgroalDataSource;

@Health
@ApplicationScoped
public class DatabaseConnectionHealthCheck implements HealthCheck {

    @Inject
    AgroalDataSource ds;

    @Override
    public HealthCheckResponse call() {

        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Database connection health check");

        try {
            simulateDatabaseConnectionVerification();
            responseBuilder.up();
        } catch (Exception e) {
            // cannot access the database
            responseBuilder.down()
                    .withData("error", e.getMessage());
        }

        return responseBuilder.build();
    }

    private void simulateDatabaseConnectionVerification() throws Exception {
        Connection conn = ds.getConnection();
        Statement sm = conn.createStatement();
        sm.execute("select * from dual");
        
    }
}