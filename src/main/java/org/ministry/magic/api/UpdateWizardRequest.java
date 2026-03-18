package org.ministry.magic.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import org.ministry.magic.core.WandCore;

public class UpdateWizardRequest {

    @JsonProperty
    private String patronus;

    @JsonProperty
    private String wandWood;

    @JsonProperty
    private WandCore wandCore;

    @JsonProperty
    private Double wandLengthInches;

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
