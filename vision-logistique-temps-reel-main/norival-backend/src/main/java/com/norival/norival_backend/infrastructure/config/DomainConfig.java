package com.norival.norival_backend.infrastructure.config;

import com.norival.norival_backend.application.usecase.ChantierUseCase;
import com.norival.norival_backend.application.usecase.ConducteurUseCase;
import com.norival.norival_backend.application.usecase.VehiculeUseCase;
import com.norival.norival_backend.domain.repository.ChantierRepository;
import com.norival.norival_backend.domain.repository.ConducteurRepository;
import com.norival.norival_backend.domain.repository.VehiculeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public ChantierUseCase chantierUseCase(ChantierRepository chantierRepository) {
        return new ChantierUseCase(chantierRepository);
    }

    @Bean
    public ConducteurUseCase conducteurUseCase(ConducteurRepository conducteurRepository) {
        return new ConducteurUseCase(conducteurRepository);
    }

    @Bean
    public VehiculeUseCase vehiculeUseCase(VehiculeRepository vehiculeRepository) {
        return new VehiculeUseCase(vehiculeRepository);
    }

    @Bean
    public com.norival.norival_backend.application.usecase.OptimizationService optimizationService(
            ChantierRepository chantierRepository, 
            VehiculeRepository vehiculeRepository,
            com.norival.norival_backend.infrastructure.persistence.repository.SpringDataRecommandationRepository recommandationRepository,
            com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository jpaVehiculeRepository) {
        return new com.norival.norival_backend.application.usecase.OptimizationService(
            chantierRepository, 
            vehiculeRepository, 
            recommandationRepository,
            jpaVehiculeRepository
        );
    }
}
