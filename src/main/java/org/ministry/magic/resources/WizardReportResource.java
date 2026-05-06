package org.ministry.magic.resources;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Path("/api/reports")
@Produces(MediaType.TEXT_PLAIN)
public class WizardReportResource {

    private static final String REPORTS_BASE_DIR = System.getProperty("ministry.reports.dir", "/var/ministry/reports");

    /**
     * Canonical base directory, resolved once at class-load time to avoid
     * repeated filesystem I/O on every request.
     */
    private static final java.nio.file.Path BASE_DIR_CANONICAL = resolveBaseDir();

    private static java.nio.file.Path resolveBaseDir() {
        try {
            return new File(REPORTS_BASE_DIR).getCanonicalFile().toPath();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot resolve reports base directory: " + REPORTS_BASE_DIR, e);
        }
    }

    @GET
    @Path("/{filename}")
    @Operation(summary = "Download a Ministry report by filename")
    public Response downloadReport(@PathParam("filename") String filename) throws IOException {
        // Prevent path traversal: reject filenames containing path separators
        if (filename == null || filename.contains("/") || filename.contains("\\")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid report filename")
                    .build();
        }

        java.nio.file.Path reportPath = BASE_DIR_CANONICAL.resolve(filename).normalize();

        // Ensure the resolved path is still within the base directory
        if (!reportPath.startsWith(BASE_DIR_CANONICAL)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid report filename")
                    .build();
        }

        File reportFile = reportPath.toFile();
        if (!reportFile.exists() || !reportFile.isFile()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Report not found")
                    .build();
        }
        String content = Files.readString(reportPath);
        return Response.ok(content).build();
    }
}
