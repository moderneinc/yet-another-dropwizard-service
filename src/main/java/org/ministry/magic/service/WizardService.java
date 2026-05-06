package org.ministry.magic.service;

import org.ministry.magic.api.CreateWizardRequest;
import org.ministry.magic.api.UpdateWizardRequest;
import org.ministry.magic.core.RegistrationStatus;
import org.ministry.magic.core.Wizard;
import org.ministry.magic.db.WizardDAO;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class WizardService {

    private static final Map<RegistrationStatus, Set<RegistrationStatus>> VALID_TRANSITIONS = Map.of(
            RegistrationStatus.ACTIVE, EnumSet.of(RegistrationStatus.SUSPENDED, RegistrationStatus.MISSING, RegistrationStatus.DECEASED),
            RegistrationStatus.SUSPENDED, EnumSet.of(RegistrationStatus.ACTIVE, RegistrationStatus.DECEASED),
            RegistrationStatus.MISSING, EnumSet.of(RegistrationStatus.ACTIVE, RegistrationStatus.DECEASED),
            RegistrationStatus.DECEASED, EnumSet.noneOf(RegistrationStatus.class)
    );

    private final WizardDAO dao;

    public WizardService(WizardDAO dao) {
        this.dao = dao;
    }

    public Wizard registerWizard(CreateWizardRequest request) {
        Wizard wizard = new Wizard();
        wizard.setFirstName(request.getFirstName());
        wizard.setLastName(request.getLastName());
        wizard.setDateOfBirth(request.getDateOfBirth());
        wizard.setHouse(request.getHouse());
        wizard.setPatronus(request.getPatronus());
        wizard.setWandWood(request.getWandWood());
        wizard.setWandCore(request.getWandCore());
        wizard.setWandLengthInches(request.getWandLengthInches());
        return dao.insert(wizard);
    }

    public Optional<Wizard> findWizard(UUID id) {
        return dao.findById(id);
    }

    public Wizard getWizard(UUID id) {
        return dao.findById(id)
                .orElseThrow(() -> new WebApplicationException("Wizard not found", Response.Status.NOT_FOUND));
    }

    public List<Wizard> listWizards() {
        return dao.findAll();
    }

    public List<Wizard> findByHouse(String house) {
        try {
            return dao.findByHouse(org.ministry.magic.core.House.valueOf(house.toUpperCase()));
        } catch (IllegalArgumentException _) {
            throw new WebApplicationException("Invalid house: " + house, Response.Status.BAD_REQUEST);
        }
    }

    public List<Wizard> findByStatus(String status) {
        try {
            return dao.findByStatus(RegistrationStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException _) {
            throw new WebApplicationException("Invalid status: " + status, Response.Status.BAD_REQUEST);
        }
    }

    public List<Wizard> searchByName(String query) {
        return dao.searchByName(query);
    }

    public Wizard updateWizard(UUID id, UpdateWizardRequest request) {
        Wizard wizard = getWizard(id);
        if (request.getPatronus() != null) {
            wizard.setPatronus(request.getPatronus());
        }
        if (request.getWandWood() != null) {
            wizard.setWandWood(request.getWandWood());
        }
        if (request.getWandCore() != null) {
            wizard.setWandCore(request.getWandCore());
        }
        if (request.getWandLengthInches() != null) {
            wizard.setWandLengthInches(request.getWandLengthInches());
        }
        dao.update(wizard);
        return wizard;
    }

    public Wizard updateStatus(UUID id, RegistrationStatus newStatus) {
        Wizard wizard = getWizard(id);
        RegistrationStatus currentStatus = wizard.getStatus();

        Set<RegistrationStatus> allowed = VALID_TRANSITIONS.get(currentStatus);
        if (allowed == null || !allowed.contains(newStatus)) {
            throw new WebApplicationException(
                    "Cannot transition from " + currentStatus + " to " + newStatus,
                    Response.Status.BAD_REQUEST);
        }

        dao.updateStatus(id, newStatus);
        wizard.setStatus(newStatus);
        return wizard;
    }

    public void deregisterWizard(UUID id) {
        Wizard wizard = getWizard(id);
        if (wizard.getStatus() == RegistrationStatus.DECEASED) {
            throw new WebApplicationException("Cannot deregister a deceased wizard", Response.Status.BAD_REQUEST);
        }
        dao.updateStatus(id, RegistrationStatus.SUSPENDED);
    }

    public long countActiveWizards() {
        return dao.countByStatus(RegistrationStatus.ACTIVE);
    }
}
