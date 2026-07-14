package com.serviteca.security.jwt;

import com.serviteca.shared.util.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class JwtSecurityContextRepository implements SecurityContextRepository {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtSecurityContextRepository(JwtTokenProvider tokenProvider, UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = authenticateFromToken(request);
        if (auth != null) {
            context.setAuthentication(auth);
        }
        return context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return extractToken(request) != null;
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        Supplier<SecurityContext> supplier = () -> {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication auth = authenticateFromToken(request);
            if (auth != null) {
                context.setAuthentication(auth);
            }
            return context;
        };
        return new SupplierDeferredSecurityContext(supplier);
    }

    private Authentication authenticateFromToken(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null || !tokenProvider.validateToken(token)) {
            return null;
        }
        String username = tokenProvider.getUsernameFromToken(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        Map<String, Object> details = new HashMap<>();
        Object empresaClaim = tokenProvider.getClaim(token, "empresaId");
        if (empresaClaim != null) {
            Long empresaId = ((Number) empresaClaim).longValue();
            details.put("empresaId", empresaId);
            TenantContext.setEmpresaId(empresaId);
        }
        Object sedeClaim = tokenProvider.getClaim(token, "sedeId");
        if (sedeClaim != null) {
            Long sedeId = ((Number) sedeClaim).longValue();
            details.put("sedeId", sedeId);
            TenantContext.setSedeId(sedeId);
        }
        auth.setDetails(details);

        return auth;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private static class SupplierDeferredSecurityContext implements DeferredSecurityContext {
        private final Supplier<SecurityContext> supplier;
        private SecurityContext context;
        private boolean generated;

        SupplierDeferredSecurityContext(Supplier<SecurityContext> supplier) {
            this.supplier = supplier;
        }

        @Override
        public SecurityContext get() {
            init();
            return context;
        }

        @Override
        public boolean isGenerated() {
            init();
            return generated;
        }

        private void init() {
            if (context == null) {
                this.context = supplier.get();
                this.generated = true;
            }
        }
    }
}
