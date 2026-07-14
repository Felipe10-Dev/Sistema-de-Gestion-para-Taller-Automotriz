package com.serviteca.audit.service;

import com.serviteca.audit.entity.AuditLog;
import com.serviteca.audit.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Async
    public void logEvent(String username, Long empresaId, Long sedeId, String ip,
                         String action, String resource, String resourceId,
                         String httpMethod, Integer httpStatus, Long durationMs,
                         boolean success, String details) {
        try {
            AuditLog audit = new AuditLog();
            audit.setTimestamp(LocalDateTime.now());
            audit.setUsername(username);
            audit.setEmpresaId(empresaId);
            audit.setSedeId(sedeId);
            audit.setIp(ip);
            audit.setAction(action);
            audit.setResource(resource);
            audit.setResourceId(resourceId);
            audit.setHttpMethod(httpMethod);
            audit.setHttpStatus(httpStatus);
            audit.setDurationMs(durationMs);
            audit.setSuccess(success);
            audit.setDetails(details);
            audit.setCreatedAt(LocalDateTime.now());
            repository.save(audit);
        } catch (Exception e) {
            log.warn("Failed to persist audit log: {}", e.getMessage());
        }
    }

    @Async
    public void logEvent(String username, Long empresaId, Long sedeId,
                         String action, String resource, boolean success) {
        logEvent(username, empresaId, sedeId, null, action, resource, null,
                null, null, null, success, null);
    }
}
