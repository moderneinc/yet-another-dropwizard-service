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

@Path("/api/reports")
@Produces(MediaType.TEXT_PLAIN)
public class WizardReportResource {

    private final java.nio.file.Path reportsBaseDir;

    public WizardReportResource() {
        this(java.nio.file.Paths.get(System.getProperty("ministry.reports.dir", "/var/ministry/reports")));
    }

    WizardReportResource(java.nio.file.Path reportsBaseDir) {
        this.reportsBaseDir = reportsBaseDir.toAbsolutePath().normalize();
    }

    @GET
    @Path("/{filename}")
    @Operation(summary = "Download a Ministry report by filename")
    public Response downloadReport(@PathParam("filename") String filename) throws IOException {
        java.nio.file.Path reportPath = reportsBaseDir.resolve(filename).normalize();
        if (!reportPath.startsWith(reportsBaseDir)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid report path")
                    .build();
        }

        if (!Files.exists(reportPath) || !Files.isRegularFile(reportPath)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Report not found: " + filename)
                    .build();
        }

        String content = Files.readString(reportPath);
        return Response.ok(content).build();
    }
}
