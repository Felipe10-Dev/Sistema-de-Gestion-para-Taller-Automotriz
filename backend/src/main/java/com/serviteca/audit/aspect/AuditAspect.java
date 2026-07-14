package com.serviteca.audit.aspect;

import com.serviteca.audit.service.AuditService;
import com.serviteca.shared.util.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);
    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @Around("execution(* com.serviteca..controller..*.*(..))")
    public Object auditControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String action = className + "." + methodName;

        HttpServletRequest request = null;
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) request = attrs.getRequest();
        } catch (Exception e) {
            // not a web request
        }

        String httpMethod = request != null ? request.getMethod() : null;
        String resource = request != null ? request.getRequestURI() : null;
        String ip = request != null ? extractIp(request) : null;

        Object result;
        boolean success = true;
        try {
            result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            int status = 200;
            if (request != null) {
                Object statusAttr = request.getAttribute("jakarta.servlet.error.status_code");
                if (statusAttr instanceof Integer) status = (Integer) statusAttr;
            }
            logAudit(request, action, resource, httpMethod, ip, status, duration, true, null);
            return result;
        } catch (Exception e) {
            success = false;
            long duration = System.currentTimeMillis() - start;
            int status = 500;
            if (request != null) {
                Object statusAttr = request.getAttribute("jakarta.servlet.error.status_code");
                if (statusAttr instanceof Integer) status = (Integer) statusAttr;
            }
            logAudit(request, action, resource, httpMethod, ip, status, duration, false,
                    e.getClass().getSimpleName() + ": " + e.getMessage());
            throw e;
        }
    }

    private void logAudit(HttpServletRequest request, String action, String resource,
                          String httpMethod, String ip, int status, long duration,
                          boolean success, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getName())) ? auth.getName() : null;
        Long empresaId = TenantContext.getEmpresaId();
        Long sedeId = TenantContext.getSedeId();

        auditService.logEvent(username, empresaId, sedeId, ip, action, resource,
                null, httpMethod, status, duration, success, details);

        if (!success) {
            log.warn("AUDIT FAIL: user={} action={} resource={} status={} duration={}ms details={}",
                    username, action, resource, status, duration, details);
        }
    }

    private String extractIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        String xri = request.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) return xri;
        return request.getRemoteAddr();
    }
}
