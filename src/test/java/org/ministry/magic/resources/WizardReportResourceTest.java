package org.ministry.magic.resources;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class WizardReportResourceTest {

    @TempDir
    Path tempDir;

    @Test
    void rejectsPathTraversal() throws IOException {
        WizardReportResource resource = new WizardReportResource(tempDir);

        Response response = resource.downloadReport("../secret.txt");

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void readsReportWithinConfiguredDirectory() throws IOException {
        Path report = tempDir.resolve("daily.txt");
        Files.writeString(report, "all good");
        WizardReportResource resource = new WizardReportResource(tempDir);

        Response response = resource.downloadReport("daily.txt");

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getEntity()).isEqualTo("all good");
    }
}
