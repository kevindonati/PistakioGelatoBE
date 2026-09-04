package kevindonati.PistakioGelatoBE.security;

import kevindonati.PistakioGelatoBE.services.SettingsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MaintenanceFilter extends OncePerRequestFilter {

    private final SettingsService settingsService;

    public MaintenanceFilter(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod();
        String path = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!settingsService.isMaintenanceMode()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isAllowedDuringMaintenance(method, path)) {
            filterChain.doFilter(request, response);
            return;
        }


        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("{\"message\":\"The website is currently under maintenance. Please try again later.\"}");
    }

    private boolean isAllowedDuringMaintenance(
            String method,
            String path
    ) {
        if ("GET".equalsIgnoreCase(method)
                && (
                "/settings/maintenance".equals(path)
                        || "/users/me".equals(path)
        )) {
            return true;
        }

        if ("PUT".equalsIgnoreCase(method)
                && "/settings/maintenance".equals(path)) {
            return true;
        }

        if ("POST".equalsIgnoreCase(method)
                && (
                "/auth/login".equals(path)
                        || "/auth/register".equals(path)
                        || "/users/forgot-password".equals(path)
                        || "/users/reset-password".equals(path)
        )) {
            return true;
        }

        if (path.startsWith("/admin/")) {
            return true;
        }

        if ("POST".equalsIgnoreCase(method)
                && "/payments/webhook".equals(path)) {
            return true;
        }

        if (
                path.equals("/swagger-ui.html")
                        || path.startsWith("/swagger-ui/")
                        || path.equals("/v3/api-docs")
                        || path.startsWith("/v3/api-docs/")
        ) {
            return true;
        }

        return false;
    }
}