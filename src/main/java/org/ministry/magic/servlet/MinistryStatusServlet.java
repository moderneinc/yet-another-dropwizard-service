package org.ministry.magic.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ministry.magic.service.WizardService;

import java.io.IOException;
import java.io.PrintWriter;

public class MinistryStatusServlet extends HttpServlet {

    private final WizardService wizardService;

    public MinistryStatusServlet(WizardService wizardService) {
        this.wizardService = wizardService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");

        long activeCount = wizardService.countActiveWizards();

        try (PrintWriter writer = resp.getWriter()) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html><head><title>Ministry of Magic — Registry Status</title></head>");
            writer.println("<body>");
            writer.println("<h1>Ministry of Magic</h1>");
            writer.println("<h2>Wizard Registry Status</h2>");
            writer.println("<p>Active registered wizards: <strong>" + activeCount + "</strong></p>");
            writer.println("<p>Service status: <strong>OPERATIONAL</strong></p>");
            writer.println("</body></html>");
        }
    }
}
