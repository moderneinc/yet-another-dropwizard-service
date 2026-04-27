package org.ministry.magic.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.ministry.magic.api.CreateWizardRequest;
import org.ministry.magic.api.UpdateWizardRequest;
import org.ministry.magic.api.WizardResponse;
import org.ministry.magic.core.RegistrationStatus;
import org.ministry.magic.core.Wizard;
import org.ministry.magic.service.WizardService;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/api/wizards")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Wizard Registry", description = "Ministry of Magic — Wizard Registration Operations")
public class WizardResource {

    private final WizardService service;

    public WizardResource(WizardService service) {
        this.service = service;
    }

    @POST
    @Operation(summary = "Register a new wizard with the Ministry")
    public Response registerWizard(@Valid CreateWizardRequest request) {
        Wizard wizard = service.registerWizard(request);
        return Response.created(URI.create("/api/wizards/" + wizard.getId()))
                .entity(WizardResponse.fromWizard(wizard))
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Retrieve a wizard by their registry ID")
    public WizardResponse getWizard(@PathParam("id") UUID id) {
        return WizardResponse.fromWizard(service.getWizard(id));
    }

    @GET
    @Operation(summary = "List all registered wizards, optionally filtered")
    public List<WizardResponse> listWizards(
            @Parameter(description = "Filter by Hogwarts house") @QueryParam("house") String house,
            @Parameter(description = "Filter by registration status") @QueryParam("status") String status,
            @Parameter(description = "Search by name") @QueryParam("q") String query) {

        List<Wizard> wizards;
        if (query != null && !query.isBlank()) {
            wizards = service.searchByName(query);
        } else if (house != null && !house.isBlank()) {
            wizards = service.findByHouse(house);
        } else if (status != null && !status.isBlank()) {
            wizards = service.findByStatus(status);
        } else {
            wizards = service.listWizards();
        }

        return wizards.stream()
                .map(WizardResponse::fromWizard)
                .collect(Collectors.toList());
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a wizard's mutable details (patronus, wand)")
    public WizardResponse updateWizard(@PathParam("id") UUID id, @Valid UpdateWizardRequest request) {
        return WizardResponse.fromWizard(service.updateWizard(id, request));
    }

    @PATCH
    @Path("/{id}/status")
    @Consumes(MediaType.TEXT_PLAIN)
    @Operation(summary = "Change a wizard's registration status")
    public WizardResponse updateStatus(@PathParam("id") UUID id, String newStatus) {
        try {
            RegistrationStatus status = RegistrationStatus.valueOf(newStatus.trim().toUpperCase());
            return WizardResponse.fromWizard(service.updateStatus(id, status));
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException("Invalid status: " + newStatus, Response.Status.BAD_REQUEST);
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deregister a wizard (soft delete — sets status to SUSPENDED)")
    public Response deregisterWizard(@PathParam("id") UUID id) {
        service.deregisterWizard(id);
        return Response.noContent().build();
    }
}
