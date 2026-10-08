package com.norival.norival_backend.infrastructure.config;

import com.norival.norival_backend.infrastructure.persistence.entity.ConducteurEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataConducteurRepository;
import com.norival.norival_backend.infrastructure.persistence.entity.ChantierEntity;
import com.norival.norival_backend.infrastructure.persistence.entity.VehiculeEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataChantierRepository;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbInitializerConfig {

    private static final Logger logger = LoggerFactory.getLogger(DbInitializerConfig.class);

    @Bean
    public CommandLineRunner initDatabase(
            SpringDataChantierRepository chantierRepository,
            SpringDataVehiculeRepository vehiculeRepository,
            SpringDataConducteurRepository conducteurRepository) {
        return args -> {
            // Seed Chantiers if empty
            if (chantierRepository.count() == 0) {
                logger.info("BDD : Initialisation des chantiers par défaut...");
                chantierRepository.save(new ChantierEntity(null, "Chantier Bouskoura", 33.4489, -7.6322, 500.0));
                chantierRepository.save(new ChantierEntity(null, "Chantier Mohammedia", 33.6860, -7.3820, 500.0));
                logger.info("BDD : 2 chantiers enregistrés.");
            }

            // Seed Conducteurs if empty
            if (conducteurRepository.count() == 0) {
                logger.info("BDD : Initialisation des conducteurs par défaut...");
                conducteurRepository.save(new ConducteurEntity(null, "El Alami", "Ahmed", "0612345678", true, "ahmed@norival.com", "pass123", "DRIVER", null));
                conducteurRepository.save(new ConducteurEntity(null, "Benjelloun", "Youssef", "0687654321", true, "youssef@norival.com", "pass456", "DRIVER", null));
                conducteurRepository.save(new ConducteurEntity(null, "Alami", "Chef Un", "0600000001", true, "chef1@norival.com", "pass123", "SITE_MANAGER", 1L));
                conducteurRepository.save(new ConducteurEntity(null, "Kabbaj", "Chef Deux", "0600000002", true, "chef2@norival.com", "pass123", "SITE_MANAGER", 2L));
                conducteurRepository.save(new ConducteurEntity(null, "Filali", "Logistique", "0600000003", true, "logistique@norival.com", "pass123", "LOGISTICS_SUPERVISOR", null));
                conducteurRepository.save(new ConducteurEntity(null, "Berrada", "Agent HSE", "0600000004", true, "hse@norival.com", "pass123", "HSE_AGENT", null));
                logger.info("BDD : Conducteurs et utilisateurs par défaut enregistrés.");
            }

            // Seed Vehicules if empty
            if (vehiculeRepository.count() == 0) {
                logger.info("BDD : Initialisation des véhicules par défaut...");
                java.util.List<ConducteurEntity> conds = conducteurRepository.findAll();
                Long ahmedId = conds.isEmpty() ? null : conds.get(0).getId();
                Long youssefId = conds.size() < 2 ? null : conds.get(1).getId();

                vehiculeRepository.save(new VehiculeEntity(null, "76192-A-26", "Camion", "DISPONIBLE", 33.4489, -7.6322, ahmedId));
                vehiculeRepository.save(new VehiculeEntity(null, "44321-B-8", "Camion", "DISPONIBLE", 33.6860, -7.3820, youssefId));
                logger.info("BDD : 2 camions enregistrés.");
            }
        };
    }
}
