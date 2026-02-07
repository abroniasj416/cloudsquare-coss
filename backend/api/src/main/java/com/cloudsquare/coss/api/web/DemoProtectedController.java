package com.cloudsquare.coss.api.web;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoProtectedController {

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        return Map.of(
                "name", authentication.getName(),
                "authorities", authentication.getAuthorities());
    }

    @GetMapping("/student/hello")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public Map<String, String> studentHello() {
        return Map.of("message", "student or admin access granted");
    }

    @GetMapping("/admin/hello")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminHello() {
        return Map.of("message", "admin access granted");
    }

    @GetMapping("/scope/read")
    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    public Map<String, String> scopeRead() {
        return Map.of("message", "scope api.read access granted");
    }
}
