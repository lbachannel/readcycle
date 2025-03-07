package com.anlb.readcycle.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.anlb.readcycle.utils.exception.MaintenanceException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;

@Component

public class CheckMaintenanceFilter implements Filter {

    @Autowired
    private ApplicationAvailability availability;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionHandler;

    // private final ApplicationAvailability availability;

    // @Qualifier("handlerExceptionResolver")
    // private HandlerExceptionResolver exceptionHandler;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (availability.getReadinessState().equals(ReadinessState.REFUSING_TRAFFIC) &&
                !((HttpServletRequest) request).getRequestURI().equals("/api/maintenance")) {
            exceptionHandler.resolveException((HttpServletRequest) request, (HttpServletResponse) response, null, new MaintenanceException("Service currently in maintenance"));
        } else {
            chain.doFilter(request, response);
        }
    }
    
}
