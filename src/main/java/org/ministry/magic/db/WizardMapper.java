package org.ministry.magic.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.ministry.magic.core.House;
import org.ministry.magic.core.RegistrationStatus;
import org.ministry.magic.core.WandCore;
import org.ministry.magic.core.Wizard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class WizardMapper implements RowMapper<Wizard> {

    @Override
    public Wizard map(ResultSet rs, StatementContext ctx) throws SQLException {
        Wizard wizard = new Wizard();
        wizard.setId(UUID.fromString(rs.getString("id")));
        wizard.setFirstName(rs.getString("first_name"));
        wizard.setLastName(rs.getString("last_name"));
        wizard.setDateOfBirth(rs.getObject("date_of_birth", LocalDate.class));
        wizard.setHouse(House.valueOf(rs.getString("house")));
        wizard.setPatronus(rs.getString("patronus"));
        wizard.setWandWood(rs.getString("wand_wood"));
        String wandCore = rs.getString("wand_core");
        if (wandCore != null) {
            wizard.setWandCore(WandCore.valueOf(wandCore));
        }
        double wandLength = rs.getDouble("wand_length_inches");
        if (!rs.wasNull()) {
            wizard.setWandLengthInches(wandLength);
        }
        wizard.setStatus(RegistrationStatus.valueOf(rs.getString("status")));
        wizard.setRegisteredAt(rs.getTimestamp("registered_at").toInstant());
        wizard.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
        return wizard;
    }
}
