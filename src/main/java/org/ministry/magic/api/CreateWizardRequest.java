package org.ministry.magic.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.ministry.magic.core.House;
import org.ministry.magic.core.WandCore;

import java.time.LocalDate;

public class CreateWizardRequest {

    @NotBlank
    @JsonProperty
    private String firstName;

    @NotBlank
    @JsonProperty
    private String lastName;

    @NotNull
    @Past
    @JsonProperty
    private LocalDate dateOfBirth;

    @NotNull
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
}
