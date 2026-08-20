package org.ministry.magic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ministry.magic.api.CreateWizardRequest;
import org.ministry.magic.api.UpdateWizardRequest;
import org.ministry.magic.core.House;
import org.ministry.magic.core.RegistrationStatus;
import org.ministry.magic.core.WandCore;
import org.ministry.magic.core.Wizard;
import org.ministry.magic.db.WizardDAO;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.WebApplicationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WizardServiceTest {

    @Mock
    private WizardDAO dao;

    private WizardService service;

    @BeforeEach
    void setUp() {
        service = new WizardService(dao);
    }

    @Test
    void registerWizardCreatesNewEntry() {
        CreateWizardRequest request = new CreateWizardRequest();
        request.setFirstName("Harry");
        request.setLastName("Potter");
        request.setDateOfBirth(LocalDate.of(1980, 7, 31));
        request.setHouse(House.GRYFFINDOR);
        request.setPatronus("Stag");
        request.setWandWood("Holly");
        request.setWandCore(WandCore.PHOENIX_FEATHER);
        request.setWandLengthInches(11.0);

        when(dao.insert(any(Wizard.class))).thenAnswer(invocation -> {
            Wizard w = invocation.getArgument(0);
            w.setId(UUID.randomUUID());
            return w;
        });

        Wizard result = service.registerWizard(request);

        assertThat(result.getFirstName()).isEqualTo("Harry");
        assertThat(result.getLastName()).isEqualTo("Potter");
        assertThat(result.getHouse()).isEqualTo(House.GRYFFINDOR);
        assertThat(result.getWandWood()).isEqualTo("Holly");
        verify(dao).insert(any(Wizard.class));
    }

    @Test
    void getWizardThrowsNotFoundForMissingId() {
        UUID id = UUID.randomUUID();
        when(dao.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getWizard(id))
                .isInstanceOf(WebApplicationException.class);
    }

    @Test
    void getWizardReturnsExistingWizard() {
        UUID id = UUID.randomUUID();
        Wizard wizard = createTestWizard(id, RegistrationStatus.ACTIVE);
        when(dao.findById(id)).thenReturn(Optional.of(wizard));

        Wizard result = service.getWizard(id);
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void updateWizardModifiesMutableFields() {
        UUID id = UUID.randomUUID();
        Wizard wizard = createTestWizard(id, RegistrationStatus.ACTIVE);
        when(dao.findById(id)).thenReturn(Optional.of(wizard));

        UpdateWizardRequest updateReq = new UpdateWizardRequest();
        updateReq.setPatronus("Phoenix");

        Wizard result = service.updateWizard(id, updateReq);
        assertThat(result.getPatronus()).isEqualTo("Phoenix");
        verify(dao).update(any(Wizard.class));
    }

    @Test
    void updateStatusAllowsValidTransition() {
        UUID id = UUID.randomUUID();
        Wizard wizard = createTestWizard(id, RegistrationStatus.ACTIVE);
        when(dao.findById(id)).thenReturn(Optional.of(wizard));

        Wizard result = service.updateStatus(id, RegistrationStatus.SUSPENDED);
        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.SUSPENDED);
        verify(dao).updateStatus(id, RegistrationStatus.SUSPENDED);
    }

    @Test
    void updateStatusRejectsInvalidTransition() {
        UUID id = UUID.randomUUID();
        Wizard wizard = createTestWizard(id, RegistrationStatus.DECEASED);
        when(dao.findById(id)).thenReturn(Optional.of(wizard));

        assertThatThrownBy(() -> service.updateStatus(id, RegistrationStatus.ACTIVE))
                .isInstanceOf(WebApplicationException.class);
    }

    @Test
    void deregisterSetsStatusToSuspended() {
        UUID id = UUID.randomUUID();
        Wizard wizard = createTestWizard(id, RegistrationStatus.ACTIVE);
        when(dao.findById(id)).thenReturn(Optional.of(wizard));

        service.deregisterWizard(id);
        verify(dao).updateStatus(id, RegistrationStatus.SUSPENDED);
    }

    @Test
    void deregisterRejectsDeceasedWizard() {
        UUID id = UUID.randomUUID();
        Wizard wizard = createTestWizard(id, RegistrationStatus.DECEASED);
        when(dao.findById(id)).thenReturn(Optional.of(wizard));

        assertThatThrownBy(() -> service.deregisterWizard(id))
                .isInstanceOf(WebApplicationException.class);
    }


    @Test
    void describeRegistrationEventHandlesKnownTypes() {
        Wizard wizard = createTestWizard(UUID.randomUUID(), RegistrationStatus.ACTIVE);

        assertThat(service.describeRegistrationEvent(wizard)).isEqualTo("Wizard registration: Test Wizard");
        assertThat(service.describeRegistrationEvent("hello")).isEqualTo("Registry message: hello");
        assertThat(service.describeRegistrationEvent(List.of("a", "b"))).isEqualTo("Batch event: 2 records");
    }

    @Test
    void getHouseDescriptionReturnsExpectedDescriptions() {
        assertThat(service.getHouseDescription(House.GRYFFINDOR))
                .isEqualTo("Brave at heart, dwell in nerve and chivalry");
        assertThat(service.getHouseDescription(House.HUFFLEPUFF))
                .isEqualTo("Just and loyal, patient and true");
        assertThat(service.getHouseDescription(House.RAVENCLAW))
                .isEqualTo("Wit beyond measure is man's greatest treasure");
        assertThat(service.getHouseDescription(House.SLYTHERIN))
                .isEqualTo("Cunning folk use any means to achieve their ends");
    }

    private Wizard createTestWizard(UUID id, RegistrationStatus status) {
        Wizard wizard = new Wizard();
        wizard.setId(id);
        wizard.setFirstName("Test");
        wizard.setLastName("Wizard");
        wizard.setDateOfBirth(LocalDate.of(1980, 1, 1));
        wizard.setHouse(House.GRYFFINDOR);
        wizard.setStatus(status);
        return wizard;
    }
}
