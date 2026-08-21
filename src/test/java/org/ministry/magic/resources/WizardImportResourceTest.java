package org.ministry.magic.resources;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.ministry.magic.service.WizardService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WizardImportResourceTest {

    @Test
    void importsSimpleWizardPayload() throws Exception {
        WizardService service = mock(WizardService.class);
        WizardImportResource resource = new WizardImportResource(service);

        String payload = "<wizards><wizard>"
                + "<firstName>Harry</firstName>"
                + "<lastName>Potter</lastName>"
                + "<dateOfBirth>1980-07-31</dateOfBirth>"
                + "<house>GRYFFINDOR</house>"
                + "<wandCore>PHOENIX_FEATHER</wandCore>"
                + "</wizard></wizards>";

        Response response = resource.importWizards(payload);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity().toString()).contains("\"imported\": 1");
        verify(service).registerWizard(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsDoctypePayload() {
        WizardService service = mock(WizardService.class);
        WizardImportResource resource = new WizardImportResource(service);

        String payload = "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>"
                + "<wizards><wizard><firstName>&xxe;</firstName></wizard></wizards>";

        assertThatThrownBy(() -> resource.importWizards(payload)).isInstanceOf(Exception.class);
    }
}
