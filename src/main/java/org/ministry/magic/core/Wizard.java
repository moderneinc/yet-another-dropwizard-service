package org.ministry.magic.core;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Wizard {

    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private House house;
    private String patronus;
    private String wandWood;
    private WandCore wandCore;
    private Double wandLengthInches;
    private RegistrationStatus status;
    private Instant registeredAt;
    private Instant updatedAt;

    public Wizard() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public String getPatronus() {
        return patronus;
    }

    public void setPatronus(String patronus) {
        this.patronus = patronus;
    }

    public String getWandWood() {
        return wandWood;
    }

    public void setWandWood(String wandWood) {
        this.wandWood = wandWood;
    }

    public WandCore getWandCore() {
        return wandCore;
    }

    public void setWandCore(WandCore wandCore) {
        this.wandCore = wandCore;
    }

    public Double getWandLengthInches() {
        return wandLengthInches;
    }

    public void setWandLengthInches(Double wandLengthInches) {
        this.wandLengthInches = wandLengthInches;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
