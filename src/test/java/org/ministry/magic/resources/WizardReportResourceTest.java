package org.ministry.magic.resources;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class WizardReportResourceTest {

    @AfterEach
    void clearProperty() {
        System.clearProperty("ministry.reports.dir");
    }

    @Test
    void rejectsPathTraversal(@TempDir Path tempDir) throws Exception {
        System.setProperty("ministry.reports.dir", tempDir.toString());
        Files.writeString(tempDir.resolve("safe.txt"), "safe report");

        WizardReportResource resource = new WizardReportResource();
        Response response = resource.downloadReport("../outside.txt");

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    void servesFileInsideConfiguredDirectory(@TempDir Path tempDir) throws Exception {
        System.setProperty("ministry.reports.dir", tempDir.toString());
        Files.writeString(tempDir.resolve("daily.txt"), "daily report");

        WizardReportResource resource = new WizardReportResource();
        Response response = resource.downloadReport("daily.txt");

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isEqualTo("daily report");
    }
}
