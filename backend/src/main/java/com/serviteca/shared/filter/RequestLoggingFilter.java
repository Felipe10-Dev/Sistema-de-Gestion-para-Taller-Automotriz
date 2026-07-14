package com.serviteca.shared.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(0)
public class RequestLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest httpReq) || !(response instanceof HttpServletResponse httpRes)) {
            chain.doFilter(request, response);
            return;
        }

        String uri = httpReq.getRequestURI();
        if (uri.startsWith("/actuator") || uri.startsWith("/h2-console")) {
            chain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            int status = httpRes.getStatus();
            String method = httpReq.getMethod();
            String query = httpReq.getQueryString();
            String fullPath = query != null ? uri + "?" + query : uri;

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String user = (auth != null && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getName())) ? auth.getName() : "-";

            if (status >= 500) {
                log.error("HTTP {} {} {} {} {}ms", method, fullPath, status, user, duration);
            } else if (status >= 400) {
                log.warn("HTTP {} {} {} {} {}ms", method, fullPath, status, user, duration);
            } else {
                log.info("HTTP {} {} {} {} {}ms", method, fullPath, status, user, duration);
            }
        }
    }
}
