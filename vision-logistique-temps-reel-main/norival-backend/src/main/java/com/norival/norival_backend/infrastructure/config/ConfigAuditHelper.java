package com.norival.norival_backend.infrastructure.config;

import com.norival.norival_backend.infrastructure.persistence.entity.ConfigurationLogEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataConfigurationLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ConfigAuditHelper {

    private final SpringDataConfigurationLogRepository logRepository;

    public ConfigAuditHelper(SpringDataConfigurationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void log(String action, String referential, Long referentialId) {
        String username = "system";
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                username = auth.getName();
            }
        } catch (Exception e) {
            // ignore
        }

        ConfigurationLogEntity logEntity = new ConfigurationLogEntity(
                null,
                username,
                action,
                referential,
                referentialId,
                LocalDateTime.now()
        );
        logRepository.save(logEntity);
    }
}
