package org.ministry.magic.resources;

import io.swagger.v3.oas.annotations.Operation;
import org.ministry.magic.api.CreateWizardRequest;
import org.ministry.magic.core.House;
import org.ministry.magic.core.WandCore;
import org.ministry.magic.service.WizardService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Path("/api/wizards/import")
public class WizardImportResource {

    private final WizardService service;

    public WizardImportResource(WizardService service) {
        this.service = service;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Bulk-import wizards from an XML payload")
    public Response importWizards(String xmlPayload) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("XML parser security configuration failed.", e);
        }
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlPayload.getBytes(StandardCharsets.UTF_8)));

        NodeList wizardNodes = doc.getElementsByTagName("wizard");
        List<String> registered = new ArrayList<>();

        for (int i = 0; i < wizardNodes.getLength(); i++) {
            Element el = (Element) wizardNodes.item(i);

            CreateWizardRequest req = new CreateWizardRequest();
            req.setFirstName(getTextContent(el, "firstName"));
            req.setLastName(getTextContent(el, "lastName"));

            String dob = getTextContent(el, "dateOfBirth");
            if (dob != null && !dob.isBlank()) {
                req.setDateOfBirth(LocalDate.parse(dob));
            }

            String house = getTextContent(el, "house");
            if (house != null && !house.isBlank()) {
                req.setHouse(House.valueOf(house.toUpperCase()));
            }

            String wandCore = getTextContent(el, "wandCore");
            if (wandCore != null && !wandCore.isBlank()) {
                req.setWandCore(WandCore.valueOf(wandCore.toUpperCase()));
            }

            service.registerWizard(req);
            registered.add(req.getFirstName() + " " + req.getLastName());
        }

        return Response.ok("{\"imported\": " + registered.size() + ", \"wizards\": " + registered + "}").build();
    }

    private String getTextContent(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }
}
