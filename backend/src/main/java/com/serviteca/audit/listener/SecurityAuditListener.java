package com.serviteca.audit.listener;

import com.serviteca.audit.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SecurityAuditListener {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuditListener.class);
    private final AuditService auditService;

    public SecurityAuditListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @EventListener
    public void onAuthSuccess(AuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        String username = auth.getName();
        String ip = extractIp();
        auditService.logEvent(username, null, null, ip, "LOGIN_SUCCESS",
                "/api/auth/login", null, "POST", 200, null, true, null);
        log.info("LOGIN OK: user={} ip={}", username, ip);
    }

    @EventListener
    public void onAuthFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        String ip = extractIp();
        auditService.logEvent(username, null, null, ip, "LOGIN_FAILED",
                "/api/auth/login", null, "POST", 401, null, false,
                "Invalid credentials");
        log.warn("LOGIN FAILED: user={} ip={}", username, ip);
    }

    private String extractIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String xff = request.getHeader("X-Forwarded-For");
                if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
                String xri = request.getHeader("X-Real-IP");
                if (xri != null && !xri.isBlank()) return xri;
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}
