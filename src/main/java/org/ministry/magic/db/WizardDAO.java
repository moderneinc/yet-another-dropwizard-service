package org.ministry.magic.db;

import org.jdbi.v3.core.Jdbi;
import org.ministry.magic.core.House;
import org.ministry.magic.core.RegistrationStatus;
import org.ministry.magic.core.Wizard;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WizardDAO {

    private final Jdbi jdbi;
    private final WizardMapper mapper = new WizardMapper();

    public WizardDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public void createTable() {
        jdbi.useHandle(handle -> handle.execute(
                "CREATE TABLE IF NOT EXISTS wizards (" +
                "  id UUID PRIMARY KEY," +
                "  first_name VARCHAR(200) NOT NULL," +
                "  last_name VARCHAR(200) NOT NULL," +
                "  date_of_birth DATE NOT NULL," +
                "  house VARCHAR(50) NOT NULL," +
                "  patronus VARCHAR(200)," +
                "  wand_wood VARCHAR(100)," +
                "  wand_core VARCHAR(100)," +
                "  wand_length_inches DOUBLE," +
                "  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'," +
                "  registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ")"
        ));
    }

    public Wizard insert(Wizard wizard) {
        Instant now = Instant.now();
        wizard.setId(UUID.randomUUID());
        wizard.setRegisteredAt(now);
        wizard.setUpdatedAt(now);
        if (wizard.getStatus() == null) {
            wizard.setStatus(RegistrationStatus.ACTIVE);
        }

        jdbi.useHandle(handle -> handle.createUpdate(
                "INSERT INTO wizards (id, first_name, last_name, date_of_birth, house, " +
                "patronus, wand_wood, wand_core, wand_length_inches, status, registered_at, updated_at) " +
                "VALUES (:id, :firstName, :lastName, :dateOfBirth, :house, " +
                ":patronus, :wandWood, :wandCore, :wandLengthInches, :status, :registeredAt, :updatedAt)")
                .bind("id", wizard.getId())
                .bind("firstName", wizard.getFirstName())
                .bind("lastName", wizard.getLastName())
                .bind("dateOfBirth", wizard.getDateOfBirth())
                .bind("house", wizard.getHouse().name())
                .bind("patronus", wizard.getPatronus())
                .bind("wandWood", wizard.getWandWood())
                .bind("wandCore", wizard.getWandCore() != null ? wizard.getWandCore().name() : null)
                .bind("wandLengthInches", wizard.getWandLengthInches())
                .bind("status", wizard.getStatus().name())
                .bind("registeredAt", Timestamp.from(wizard.getRegisteredAt()))
                .bind("updatedAt", Timestamp.from(wizard.getUpdatedAt()))
                .execute()
        );
        return wizard;
    }

    public Optional<Wizard> findById(UUID id) {
        return jdbi.withHandle(handle -> handle.createQuery(
                "SELECT * FROM wizards WHERE id = :id")
                .bind("id", id)
                .map(mapper)
                .findFirst()
        );
    }

    public List<Wizard> findAll() {
        return jdbi.withHandle(handle -> handle.createQuery(
                "SELECT * FROM wizards ORDER BY last_name, first_name")
                .map(mapper)
                .list()
        );
    }

    public List<Wizard> findByHouse(House house) {
        return jdbi.withHandle(handle -> handle.createQuery(
                "SELECT * FROM wizards WHERE house = :house ORDER BY last_name, first_name")
                .bind("house", house.name())
                .map(mapper)
                .list()
        );
    }

    public List<Wizard> findByStatus(RegistrationStatus status) {
        return jdbi.withHandle(handle -> handle.createQuery(
                "SELECT * FROM wizards WHERE status = :status ORDER BY last_name, first_name")
                .bind("status", status.name())
                .map(mapper)
                .list()
        );
    }

    public List<Wizard> searchByName(String query) {
        // Escape LIKE metacharacters so that '%' and '_' in user input are treated as literals
        String escaped = query.toLowerCase()
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        String pattern = "%" + escaped + "%";
        return jdbi.withHandle(handle -> handle.createQuery(
                "SELECT * FROM wizards WHERE LOWER(first_name) LIKE :pattern ESCAPE '!' " +
                "OR LOWER(last_name) LIKE :pattern ESCAPE '!' " +
                "ORDER BY last_name, first_name")
                .bind("pattern", pattern)
                .map(mapper)
                .list()
        );
    }

    public void update(Wizard wizard) {
        wizard.setUpdatedAt(Instant.now());
        jdbi.useHandle(handle -> handle.createUpdate(
                "UPDATE wizards SET patronus = :patronus, wand_wood = :wandWood, " +
                "wand_core = :wandCore, wand_length_inches = :wandLengthInches, " +
                "status = :status, updated_at = :updatedAt WHERE id = :id")
                .bind("id", wizard.getId())
                .bind("patronus", wizard.getPatronus())
                .bind("wandWood", wizard.getWandWood())
                .bind("wandCore", wizard.getWandCore() != null ? wizard.getWandCore().name() : null)
                .bind("wandLengthInches", wizard.getWandLengthInches())
                .bind("status", wizard.getStatus().name())
                .bind("updatedAt", Timestamp.from(wizard.getUpdatedAt()))
                .execute()
        );
    }

    public void updateStatus(UUID id, RegistrationStatus status) {
        jdbi.useHandle(handle -> handle.createUpdate(
                "UPDATE wizards SET status = :status, updated_at = :updatedAt WHERE id = :id")
                .bind("id", id)
                .bind("status", status.name())
                .bind("updatedAt", Timestamp.from(Instant.now()))
                .execute()
        );
    }

    public long countByStatus(RegistrationStatus status) {
        return jdbi.withHandle(handle -> handle.createQuery(
                "SELECT COUNT(*) FROM wizards WHERE status = :status")
                .bind("status", status.name())
                .mapTo(Long.class)
                .one()
        );
    }
}
