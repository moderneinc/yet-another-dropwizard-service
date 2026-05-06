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

    @GET
    @Path("/{filename}")
    @Operation(summary = "Download a Ministry report by filename")
    public Response downloadReport(@PathParam("filename") String filename) throws IOException {
        // Prevent path traversal: reject filenames containing separators or parent-directory references
        if (filename == null || filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid report filename")
                    .build();
        }

        File baseDir = new File(REPORTS_BASE_DIR).getCanonicalFile();
        File reportFile = new File(baseDir, filename).getCanonicalFile();

        // Ensure the resolved path is still within the base directory
        if (!reportFile.toPath().startsWith(baseDir.toPath())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid report filename")
                    .build();
        }

        if (!reportFile.exists() || !reportFile.isFile()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Report not found")
                    .build();
        }
        String content = Files.readString(reportFile.toPath());
        return Response.ok(content).build();
    }
}
