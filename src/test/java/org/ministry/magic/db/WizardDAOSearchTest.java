package org.ministry.magic.db;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ministry.magic.core.House;
import org.ministry.magic.core.RegistrationStatus;
import org.ministry.magic.core.Wizard;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for WizardDAO.searchByName, focused on LIKE metacharacter escaping
 * to prevent wildcard injection.
 */
class WizardDAOSearchTest {

    private Jdbi jdbi;
    private WizardDAO dao;

    @BeforeEach
    void setUp() {
        jdbi = Jdbi.create("jdbc:h2:mem:search-test-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        dao = new WizardDAO(jdbi);
        dao.createTable();

        // Insert controlled test data
        insertWizard("Harry", "Potter");            // normal name
        insertWizard("Her%mione", "Gr_anger");       // names containing LIKE metacharacters
        insertWizard("Tom!Riddle", "Vo!ldemort");    // names containing the escape character '!'
        insertWizard("Albus", "Dumbledore");         // baseline — should NOT match the metachar searches
    }

    @AfterEach
    void tearDown() {
        jdbi.useHandle(h -> h.execute("DROP TABLE IF EXISTS wizards"));
    }

    @Test
    void searchByLiteralPercentDoesNotMatchAllNames() {
        // '%' in query must NOT be treated as a wildcard — only the wizard whose
        // name literally contains '%' should be returned.
        List<Wizard> results = dao.searchByName("%");
        assertThat(results)
                .extracting(Wizard::getFirstName)
                .containsExactly("Her%mione");
    }

    @Test
    void searchByLiteralUnderscoreMatchesOnlyLiteralUnderscore() {
        // '_' in query must NOT act as a single-character wildcard.
        List<Wizard> results = dao.searchByName("_");
        assertThat(results)
                .extracting(Wizard::getLastName)
                .containsExactly("Gr_anger");
    }

    @Test
    void searchByEscapeCharacterMatchesLiterally() {
        // '!' (our escape char) in query must be treated as a literal.
        // The wizard with firstName="Tom!Riddle" and lastName="Vo!ldemort" both contain '!'.
        // Since OR in the WHERE clause matches the same row, only one result is returned.
        List<Wizard> results = dao.searchByName("!");
        assertThat(results)
                .hasSize(1)
                .extracting(Wizard::getFirstName)
                .containsExactly("Tom!Riddle");
    }

    @Test
    void searchWithCombinedEscapeAndMetacharacters() {
        // Input "!%" — after escaping becomes "!!!!%" in the pattern, i.e. literal "!%".
        // No wizard name contains the literal two-character sequence "!%".
        List<Wizard> results = dao.searchByName("!%");
        assertThat(results).isEmpty();
    }

    @Test
    void searchNormalQueryStillWorks() {
        List<Wizard> results = dao.searchByName("pot");
        assertThat(results)
                .extracting(Wizard::getLastName)
                .containsExactly("Potter");
    }

    // -------------------------------------------------------------------------

    private void insertWizard(String firstName, String lastName) {
        Wizard w = new Wizard();
        w.setFirstName(firstName);
        w.setLastName(lastName);
        w.setDateOfBirth(LocalDate.of(1980, 1, 1));
        w.setHouse(House.GRYFFINDOR);
        w.setStatus(RegistrationStatus.ACTIVE);
        dao.insert(w);
    }
}
