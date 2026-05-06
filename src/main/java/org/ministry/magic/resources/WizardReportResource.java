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
        File reportsDir = new File(REPORTS_BASE_DIR);
        File reportFile = new File(reportsDir, filename);
        if (!reportFile.getCanonicalPath().startsWith(reportsDir.getCanonicalPath() + File.separator)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid filename")
                    .build();
        }
        if (!reportFile.exists() || !reportFile.isFile()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Report not found: " + filename)
                    .build();
        }
        String content = Files.readString(reportFile.toPath());
        return Response.ok(content).build();
    }
}
