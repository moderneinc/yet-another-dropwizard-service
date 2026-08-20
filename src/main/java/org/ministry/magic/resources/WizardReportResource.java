package org.ministry.magic.resources;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/api/reports")
@Produces(MediaType.TEXT_PLAIN)
public class WizardReportResource {

    private static final String REPORTS_DIR_PROPERTY = "ministry.reports.dir";
    private static final String DEFAULT_REPORTS_BASE_DIR = "/var/ministry/reports";

    @GET
    @Path("/{filename}")
    @Operation(summary = "Download a Ministry report by filename")
    public Response downloadReport(@PathParam("filename") String filename) throws IOException {
        java.nio.file.Path baseDir = Paths.get(System.getProperty(REPORTS_DIR_PROPERTY, DEFAULT_REPORTS_BASE_DIR))
                .toAbsolutePath()
                .normalize();
        java.nio.file.Path reportFile = baseDir.resolve(filename).normalize();

        if (!reportFile.startsWith(baseDir) || !Files.isRegularFile(reportFile)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Report not found: " + filename)
                    .build();
        }

        String content = Files.readString(reportFile);
        return Response.ok(content).build();
    }
}
