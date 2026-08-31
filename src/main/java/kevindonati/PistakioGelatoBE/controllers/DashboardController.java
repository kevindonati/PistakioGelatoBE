package kevindonati.PistakioGelatoBE.controllers;

import kevindonati.PistakioGelatoBE.payloads.DashboardStatsDTO;
import kevindonati.PistakioGelatoBE.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardStatsDTO getStats(@RequestParam(defaultValue = "MONTH") String period) {
        return dashboardService.getStats(period);
    }
}