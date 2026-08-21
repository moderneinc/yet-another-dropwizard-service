package org.ministry.magic.resources;

import org.junit.jupiter.api.Test;
import org.ministry.magic.service.WizardService;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class WizardImportResourceTest {

    @Test
    void rejectsXmlWithDoctype() {
        WizardService service = mock(WizardService.class);
        WizardImportResource resource = new WizardImportResource(service);

        String xml = "<!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>"
                + "<wizards><wizard><firstName>&xxe;</firstName><lastName>Test</lastName></wizard></wizards>";

        assertThatThrownBy(() -> resource.importWizards(xml))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("DOCTYPE");

        verifyNoInteractions(service);
    }
}
