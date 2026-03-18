package org.ministry.magic;

import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ministry.magic.api.CreateWizardRequest;
import org.ministry.magic.api.UpdateWizardRequest;
import org.ministry.magic.api.WizardResponse;
import org.ministry.magic.core.House;
import org.ministry.magic.core.WandCore;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
class WizardRegistryApplicationIT {

    private static final DropwizardAppExtension<WizardRegistryConfiguration> APP =
            new DropwizardAppExtension<>(
                    WizardRegistryApplication.class,
                    ResourceHelpers.resourceFilePath("test-wizard-registry.yml"),
                    ConfigOverride.config("database.url", "jdbc:h2:mem:test-it-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1;MODE=PostgreSQL")
            );

    private static Client client;

    @BeforeAll
    static void setUp() {
        client = APP.client();
    }

    private String wizardsUrl() {
        return "http://localhost:" + APP.getLocalPort() + "/api/wizards";
    }

    @Test
    void applicationStartsSuccessfully() {
        assertThat((Object) APP.getApplication()).isNotNull();
        assertThat(APP.getConfiguration()).isNotNull();
        assertThat(APP.getConfiguration().getDataSourceFactory()).isNotNull();
    }

    @Test
    void canRetrieveConfiguration() {
        WizardRegistryConfiguration config = APP.getConfiguration();
        assertThat(config.getDataSourceFactory().getUrl()).contains("h2:mem");
        assertThat(config.getDataSourceFactory().getUser()).isEqualTo("sa");
    }

    @Test
    void listWizardsReturnsSeededData() {
        List<WizardResponse> wizards = client.target(wizardsUrl())
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<WizardResponse>>() {});

        assertThat(wizards).isNotEmpty();
        assertThat(wizards).extracting(WizardResponse::getLastName)
                .contains("Potter", "Granger", "Weasley");
    }

    @Test
    void canRegisterAndRetrieveWizard() {
        CreateWizardRequest request = new CreateWizardRequest();
        request.setFirstName("Albus");
        request.setLastName("Dumbledore");
        request.setDateOfBirth(LocalDate.of(1881, 7, 1));
        request.setHouse(House.GRYFFINDOR);
        request.setPatronus("Phoenix");
        request.setWandWood("Elder");
        request.setWandCore(WandCore.PHOENIX_FEATHER);
        request.setWandLengthInches(15.0);

        Response createResponse = client.target(wizardsUrl())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));

        assertThat(createResponse.getStatus()).isEqualTo(201);

        WizardResponse created = createResponse.readEntity(WizardResponse.class);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getFirstName()).isEqualTo("Albus");
        assertThat(created.getLastName()).isEqualTo("Dumbledore");
        assertThat(created.getPatronus()).isEqualTo("Phoenix");

        // Retrieve by ID
        WizardResponse retrieved = client.target(wizardsUrl() + "/" + created.getId())
                .request(MediaType.APPLICATION_JSON)
                .get(WizardResponse.class);

        assertThat(retrieved.getId()).isEqualTo(created.getId());
        assertThat(retrieved.getFirstName()).isEqualTo("Albus");
    }

    @Test
    void canUpdateWizard() {
        // First create a wizard
        CreateWizardRequest createReq = new CreateWizardRequest();
        createReq.setFirstName("Severus");
        createReq.setLastName("Snape");
        createReq.setDateOfBirth(LocalDate.of(1960, 1, 9));
        createReq.setHouse(House.SLYTHERIN);

        WizardResponse created = client.target(wizardsUrl())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(createReq))
                .readEntity(WizardResponse.class);

        // Update patronus and wand
        UpdateWizardRequest updateReq = new UpdateWizardRequest();
        updateReq.setPatronus("Doe");
        updateReq.setWandWood("Birch");
        updateReq.setWandCore(WandCore.DRAGON_HEARTSTRING);

        WizardResponse updated = client.target(wizardsUrl() + "/" + created.getId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(updateReq))
                .readEntity(WizardResponse.class);

        assertThat(updated.getPatronus()).isEqualTo("Doe");
        assertThat(updated.getWandWood()).isEqualTo("Birch");
    }

    @Test
    void canFilterByHouse() {
        List<WizardResponse> gryffindors = client.target(wizardsUrl())
                .queryParam("house", "GRYFFINDOR")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<WizardResponse>>() {});

        assertThat(gryffindors).isNotEmpty();
        assertThat(gryffindors).allMatch(w -> w.getHouse() == House.GRYFFINDOR);
    }

    @Test
    void canSearchByName() {
        List<WizardResponse> results = client.target(wizardsUrl())
                .queryParam("q", "Potter")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<WizardResponse>>() {});

        assertThat(results).isNotEmpty();
        assertThat(results).extracting(WizardResponse::getLastName).contains("Potter");
    }

    @Test
    void canUpdateStatus() {
        CreateWizardRequest createReq = new CreateWizardRequest();
        createReq.setFirstName("Sirius");
        createReq.setLastName("Black");
        createReq.setDateOfBirth(LocalDate.of(1959, 11, 3));
        createReq.setHouse(House.GRYFFINDOR);

        WizardResponse created = client.target(wizardsUrl())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(createReq))
                .readEntity(WizardResponse.class);

        // Suspend the wizard
        Response statusResponse = client.target(wizardsUrl() + "/" + created.getId() + "/status")
                .request(MediaType.APPLICATION_JSON)
                .method("PATCH", Entity.text("SUSPENDED"));

        assertThat(statusResponse.getStatus()).isEqualTo(200);
    }

    @Test
    void canDeregisterWizard() {
        CreateWizardRequest createReq = new CreateWizardRequest();
        createReq.setFirstName("Peter");
        createReq.setLastName("Pettigrew");
        createReq.setDateOfBirth(LocalDate.of(1960, 6, 15));
        createReq.setHouse(House.GRYFFINDOR);

        WizardResponse created = client.target(wizardsUrl())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(createReq))
                .readEntity(WizardResponse.class);

        Response deleteResponse = client.target(wizardsUrl() + "/" + created.getId())
                .request()
                .delete();

        assertThat(deleteResponse.getStatus()).isEqualTo(204);
    }

    @Test
    void notFoundForMissingWizard() {
        Response response = client.target(wizardsUrl() + "/" + UUID.randomUUID())
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    void testSupportAccessible() {
        assertThat(APP.getTestSupport()).isNotNull();
        assertThat(APP.getTestSupport().getEnvironment()).isNotNull();
    }
}
