package org.ministry.magic.managed;

import io.dropwizard.lifecycle.Managed;
import org.ministry.magic.core.House;
import org.ministry.magic.core.WandCore;
import org.ministry.magic.core.Wizard;
import org.ministry.magic.db.WizardDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class WizardRegistryManaged implements Managed {

    private static final Logger log = LoggerFactory.getLogger(WizardRegistryManaged.class);

    private final WizardDAO dao;

    public WizardRegistryManaged(WizardDAO dao) {
        this.dao = dao;
    }

    @Override
    public void start() throws Exception {
        log.info("Initializing Wizard Registry — creating schema and seeding data");
        dao.createTable();
        seedData();
        log.info("Wizard Registry initialized successfully");
    }

    @Override
    public void stop() throws Exception {
        log.info("Wizard Registry shutting down — Ministry of Magic offline");
    }

    private void seedData() {
        if (dao.findAll().isEmpty()) {
            log.info("Seeding registry with known wizards");

            insertWizard("Harry", "Potter", LocalDate.of(1980, 7, 31),
                    House.GRYFFINDOR, "Stag", "Holly", WandCore.PHOENIX_FEATHER, 11.0);
            insertWizard("Hermione", "Granger", LocalDate.of(1979, 9, 19),
                    House.GRYFFINDOR, "Otter", "Vine", WandCore.DRAGON_HEARTSTRING, 10.75);
            insertWizard("Ron", "Weasley", LocalDate.of(1980, 3, 1),
                    House.GRYFFINDOR, "Jack Russell Terrier", "Willow", WandCore.UNICORN_HAIR, 14.0);
            insertWizard("Draco", "Malfoy", LocalDate.of(1980, 6, 5),
                    House.SLYTHERIN, null, "Hawthorn", WandCore.UNICORN_HAIR, 10.0);
            insertWizard("Luna", "Lovegood", LocalDate.of(1981, 2, 13),
                    House.RAVENCLAW, "Hare", null, null, null);
            insertWizard("Neville", "Longbottom", LocalDate.of(1980, 7, 30),
                    House.GRYFFINDOR, null, "Cherry", WandCore.UNICORN_HAIR, 13.0);
            insertWizard("Cedric", "Diggory", LocalDate.of(1977, 9, 1),
                    House.HUFFLEPUFF, null, "Ash", WandCore.UNICORN_HAIR, 12.25);
            insertWizard("Cho", "Chang", LocalDate.of(1979, 4, 7),
                    House.RAVENCLAW, "Swan", null, null, null);
            insertWizard("Nymphadora", "Tonks", LocalDate.of(1973, 3, 15),
                    House.HUFFLEPUFF, "Jack Rabbit", null, null, null);
            insertWizard("Kingsley", "Shacklebolt", LocalDate.of(1960, 8, 26),
                    House.NONE, "Lynx", null, null, null);

            log.info("Seeded {} wizards into the registry", 10);
        }
    }

    private void insertWizard(String firstName, String lastName, LocalDate dob,
                               House house, String patronus, String wandWood,
                               WandCore wandCore, Double wandLength) {
        Wizard wizard = new Wizard();
        wizard.setFirstName(firstName);
        wizard.setLastName(lastName);
        wizard.setDateOfBirth(dob);
        wizard.setHouse(house);
        wizard.setPatronus(patronus);
        wizard.setWandWood(wandWood);
        wizard.setWandCore(wandCore);
        wizard.setWandLengthInches(wandLength);
        dao.insert(wizard);
    }
}
