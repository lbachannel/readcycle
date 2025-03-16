package com.anlb.readcycle.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.anlb.readcycle.service.IMaintenanceService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MaintenanceInterceptor implements HandlerInterceptor {

    private final IMaintenanceService maintenanceService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (maintenanceService.getMaintenance().isMaintenanceMode()) {

            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.setContentType("application/json");

            Map<String, Object> notifyMaintenanceMode = new HashMap<>();
            notifyMaintenanceMode.put("statusCode", HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            notifyMaintenanceMode.put("message", "Maintenance mode, we will be back soon");

            String jsonResponse = new ObjectMapper().writeValueAsString(notifyMaintenanceMode);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
            return false;
        }
        return true;
    }
}
