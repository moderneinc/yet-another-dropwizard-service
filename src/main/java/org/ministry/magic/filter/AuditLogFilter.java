package org.ministry.magic.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AuditLogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuditLogFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("Ministry Audit Log Filter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        long startTime = System.currentTimeMillis();

        chain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        log.info("AUDIT: {} {} — status={} duration={}ms remoteAddr={}",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                httpResponse.getStatus(),
                duration,
                httpRequest.getRemoteAddr());
    }

    @Override
    public void destroy() {
        log.info("Ministry Audit Log Filter destroyed");
    }
}
