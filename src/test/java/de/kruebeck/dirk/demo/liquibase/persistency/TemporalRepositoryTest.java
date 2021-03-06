package de.kruebeck.dirk.demo.liquibase.persistency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.kruebeck.dirk.demo.liquibase.TestUtils.createAndSaveRandomEntities;
import static de.kruebeck.dirk.demo.liquibase.TestUtils.createRandomEntity;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class TemporalRepositoryTest {

    @Autowired
    protected TemporalRepository temporalRepository;

    @AfterEach
    public void cleanup() {
        temporalRepository.deleteAll();
    }

    @Test
    public void crudTest() throws Exception {

        // CREATE
        TemporalEntity te = createRandomEntity("test");
        UUID uuid = te.getId();

        temporalRepository.save(te);

        assertEquals(1, temporalRepository.count());

        // READ
        Optional<TemporalEntity> existing = temporalRepository.findById(uuid);
        assertTrue(existing.isPresent());

        Optional<TemporalEntity> nonExisting = temporalRepository.findById(UUID.randomUUID());
        assertFalse(nonExisting.isPresent());

        TemporalEntity created = existing.get();
        Instant createdModifiedAt = created.getModifiedAt();

        // UPDATE

        // We need some difference ...
        Thread.sleep(10);
        created.setText("Holterdipolter");
        temporalRepository.save(created);

        Optional<TemporalEntity> updated = temporalRepository.findById(uuid);
        assertTrue(updated.isPresent());

        assertTrue(0 != createdModifiedAt.compareTo(updated.get().getModifiedAt()));

        // DELETE
        temporalRepository.deleteById(uuid);
        Optional<TemporalEntity> deleted = temporalRepository.findById(uuid);
        assertFalse(nonExisting.isPresent());

        assertEquals(0, temporalRepository.count());
    }

    @Test
    public void testQueryVariant1() throws Exception {

        createAndSaveRandomEntities(temporalRepository, "Holerdipolter", 10);
        createAndSaveRandomEntities(temporalRepository, "Holerdipolter", 10);
        createAndSaveRandomEntities(temporalRepository, "Holerdipolter", 10);

        assertEquals(30, temporalRepository.count());

        List<TemporalEntity> findList = temporalRepository.findByNameAndValidity("Holerdipolter-0", Instant.now());

        assertEquals(3, findList.size());

        findList = temporalRepository.findByNameAndValidity("Holerdipolter-0", Instant.now().plus(100, ChronoUnit.DAYS));

        assertEquals(0, findList.size());
    }

    @Test
    public void testQueryVariant2() throws Exception {

        createAndSaveRandomEntities(temporalRepository, "Holerdipolter", 10);
        createAndSaveRandomEntities(temporalRepository, "Holerdipolter", 10);
        createAndSaveRandomEntities(temporalRepository, "Holerdipolter", 10);

        assertEquals(30, temporalRepository.count());

        List<TemporalEntity> findList = temporalRepository.findByNameAndValidityVar2("Holerdipolter-0", Instant.now());

        assertEquals(3, findList.size());

        findList = temporalRepository.findByNameAndValidityVar2("Holerdipolter-0", Instant.now().plus(100, ChronoUnit.DAYS));

        assertEquals(0, findList.size());
    }


}
