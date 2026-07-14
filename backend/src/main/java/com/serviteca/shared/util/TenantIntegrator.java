package com.serviteca.shared.util;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class TenantIntegrator implements Integrator {

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory,
                          SessionFactoryServiceRegistry serviceRegistry) {
        EventListenerRegistry registry = serviceRegistry.getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(new TenantInsertListener());
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory,
                             SessionFactoryServiceRegistry serviceRegistry) {
    }

    private static class TenantInsertListener implements PreInsertEventListener {
        @Override
        public boolean onPreInsert(PreInsertEvent event) {
            Object entity = event.getEntity();
            if (entity instanceof BaseEntity) {
                BaseEntity be = (BaseEntity) entity;
                if (be.getEmpresaId() == null) {
                    Long id = resolveEmpresaId();
                    if (id != null) {
                        be.setEmpresaId(id);
                        event.getState()[getPropertyIndex(event, "empresaId")] = id;
                    }
                }
                if (be.getSedeId() == null) {
                    Long id = resolveSedeId();
                    if (id != null) {
                        be.setSedeId(id);
                        event.getState()[getPropertyIndex(event, "sedeId")] = id;
                    }
                }
            }
            return false;
        }

        private Long resolveEmpresaId() {
            Long id = TenantContext.getEmpresaId();
            if (id == null) {
                var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && auth.getDetails() instanceof java.util.Map) {
                    Object v = ((java.util.Map<?, ?>) auth.getDetails()).get("empresaId");
                    if (v != null) id = ((Number) v).longValue();
                }
            }
            return id;
        }

        private Long resolveSedeId() {
            Long id = TenantContext.getSedeId();
            if (id == null) {
                var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && auth.getDetails() instanceof java.util.Map) {
                    Object v = ((java.util.Map<?, ?>) auth.getDetails()).get("sedeId");
                    if (v != null) id = ((Number) v).longValue();
                }
            }
            return id;
        }

        private int getPropertyIndex(PreInsertEvent event, String propertyName) {
            String[] propNames = event.getPersister().getEntityMetamodel().getPropertyNames();
            for (int i = 0; i < propNames.length; i++) {
                if (propNames[i].equals(propertyName)) return i;
            }
            return -1;
        }
    }
}
