package com.norival.norival_backend;

import com.norival.norival_backend.application.usecase.ChantierUseCase;
import com.norival.norival_backend.domain.model.Chantier;
import com.norival.norival_backend.infrastructure.rest.ChantierController;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository;
import com.norival.norival_backend.infrastructure.config.ConfigAuditHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ChantierControllerTest {

    @Mock
    private ChantierUseCase chantierUseCase;

    @Mock
    private SpringDataVehiculeRepository jpaVehiculeRepository;

    @Mock
    private ConfigAuditHelper auditHelper;

    @InjectMocks
    private ChantierController chantierController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChantiers() {
        Chantier chantier1 = new Chantier();
        chantier1.setId(1L);
        chantier1.setNom("Chantier Test 1");

        Chantier chantier2 = new Chantier();
        chantier2.setId(2L);
        chantier2.setNom("Chantier Test 2");

        when(chantierUseCase.obtenirTousLesChantiers()).thenReturn(Arrays.asList(chantier1, chantier2));

        List<Chantier> result = chantierController.getChantiers();

        assertEquals(2, result.size());
        assertEquals("Chantier Test 1", result.get(0).getNom());
        verify(chantierUseCase, times(1)).obtenirTousLesChantiers();
    }

    @Test
    void testGetChantierById_Found() {
        Chantier chantier = new Chantier();
        chantier.setId(1L);
        when(chantierUseCase.obtenirChantierParId(1L)).thenReturn(Optional.of(chantier));

        ResponseEntity<Chantier> response = chantierController.getChantierById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testUpdateChantier_RG06_Violation() {
        // Test RG-06: Cannot close a site if it has vehicles
        Chantier chantier = new Chantier();
        chantier.setStatut("CLOTURE");

        when(jpaVehiculeRepository.existsByChantierId(1L)).thenReturn(true);

        ResponseEntity<?> response = chantierController.updateChantier(1L, chantier);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(
                "RG-06: Un chantier ne peut être clôturé dans le système que si l'ensemble de sa flotte affectée a été réaffectée ou libérée.",
                response.getBody());
    }
}
