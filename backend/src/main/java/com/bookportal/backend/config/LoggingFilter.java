package com.bookportal.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String TRACE_ID = "traceId";
    private static final String USER_ID = "userId";
    private static final String IP = "ip";
    private static final String METHOD = "method";
    private static final String PATH = "path";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        MDC.put(TRACE_ID, traceId);
        MDC.put(IP, getClientIp(request));
        MDC.put(METHOD, request.getMethod());
        MDC.put(PATH, request.getRequestURI());

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (Exception e) {
            throw e;
        } finally {
            responseWrapper.copyBodyToResponse();
            
            long duration = System.currentTimeMillis() - startTime;
            int status = responseWrapper.getStatus();
            
            log.info("Request completed - method={} path={} status={} duration={}ms",
                    request.getMethod(), request.getRequestURI(), status, duration);
            
            MDC.remove(TRACE_ID);
            MDC.remove(IP);
            MDC.remove(METHOD);
            MDC.remove(PATH);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
