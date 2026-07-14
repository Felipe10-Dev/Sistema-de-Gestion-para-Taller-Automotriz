package com.serviteca.shared.util;

import jakarta.persistence.PrePersist;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class TenantEntityListener {

    @PrePersist
    public void setTenant(Object entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity be = (BaseEntity) entity;
            if (be.getEmpresaId() == null) {
                Long empresaId = TenantContext.getEmpresaId();
                if (empresaId == null) {
                    empresaId = extractEmpresaIdFromAuth();
                }
                if (empresaId != null) {
                    be.setEmpresaId(empresaId);
                }
            }
            if (be.getSedeId() == null) {
                Long sedeId = TenantContext.getSedeId();
                if (sedeId == null) {
                    sedeId = extractSedeIdFromAuth();
                }
                if (sedeId != null) {
                    be.setSedeId(sedeId);
                }
            }
        }
    }

    private Long extractEmpresaIdFromAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getDetails() instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> details = (java.util.Map<String, Object>) auth.getDetails();
            Object val = details.get("empresaId");
            if (val != null) return ((Number) val).longValue();
        }
        return null;
    }

    private Long extractSedeIdFromAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getDetails() instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> details = (java.util.Map<String, Object>) auth.getDetails();
            Object val = details.get("sedeId");
            if (val != null) return ((Number) val).longValue();
        }
        return null;
    }
}
