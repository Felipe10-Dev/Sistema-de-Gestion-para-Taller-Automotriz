package com.serviteca.shared.util;

import java.lang.reflect.Field;

/**
 * Utility methods for setting tenant fields on entities.
 * Can be called from any entity's @PrePersist method.
 */
public class TenantUtil {

    private TenantUtil() {}

    private static Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return findField(superClass, name);
            }
            throw e;
        }
    }

    public static void setTenantFields(Object entity) {
        if (entity == null) return;
        try {
            Field empresaField = findField(entity.getClass(), "empresaId");
            empresaField.setAccessible(true);
            if (empresaField.get(entity) != null) return;

            Long empresaId = resolveEmpresaId();
            empresaField.set(entity, empresaId != null ? empresaId : 1L);

            try {
                Field sedeField = findField(entity.getClass(), "sedeId");
                sedeField.setAccessible(true);
                if (sedeField.get(entity) != null) return;
                Long sedeId = resolveSedeId();
                sedeField.set(entity, sedeId != null ? sedeId : 1L);
            } catch (NoSuchFieldException e) {
                // entity doesn't have sedeId field, skip
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private static Long resolveEmpresaId() {
        Long id = TenantContext.getEmpresaId();
        if (id != null) return id;
        try {
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getDetails() instanceof java.util.Map) {
                Object v = ((java.util.Map<?, ?>) auth.getDetails()).get("empresaId");
                if (v != null) return ((Number) v).longValue();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static Long resolveSedeId() {
        Long id = TenantContext.getSedeId();
        if (id != null) return id;
        try {
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getDetails() instanceof java.util.Map) {
                Object v = ((java.util.Map<?, ?>) auth.getDetails()).get("sedeId");
                if (v != null) return ((Number) v).longValue();
            }
        } catch (Exception ignored) {}
        return null;
    }
}
