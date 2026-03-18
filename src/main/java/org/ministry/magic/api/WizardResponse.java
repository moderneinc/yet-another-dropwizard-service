package org.ministry.magic.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ministry.magic.core.House;
import org.ministry.magic.core.RegistrationStatus;
import org.ministry.magic.core.WandCore;
import org.ministry.magic.core.Wizard;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class WizardResponse {

    @JsonProperty
    private UUID id;

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private LocalDate dateOfBirth;

    @JsonProperty
    private House house;

    @JsonProperty
    private String patronus;

    @JsonProperty
    private String wandWood;

    @JsonProperty
    private WandCore wandCore;

    @JsonProperty
    private Double wandLengthInches;

    @JsonProperty
    private RegistrationStatus status;

    @JsonProperty
    private Instant registeredAt;

    @JsonProperty
    private Instant updatedAt;

    public static WizardResponse fromWizard(Wizard wizard) {
        WizardResponse response = new WizardResponse();
        response.id = wizard.getId();
        response.firstName = wizard.getFirstName();
        response.lastName = wizard.getLastName();
        response.dateOfBirth = wizard.getDateOfBirth();
        response.house = wizard.getHouse();
        response.patronus = wizard.getPatronus();
        response.wandWood = wizard.getWandWood();
        response.wandCore = wizard.getWandCore();
        response.wandLengthInches = wizard.getWandLengthInches();
        response.status = wizard.getStatus();
        response.registeredAt = wizard.getRegisteredAt();
        response.updatedAt = wizard.getUpdatedAt();
        return response;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public House getHouse() {
        return house;
    }

    public String getPatronus() {
        return patronus;
    }

    public String getWandWood() {
        return wandWood;
    }

    public WandCore getWandCore() {
        return wandCore;
    }

    public Double getWandLengthInches() {
        return wandLengthInches;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
