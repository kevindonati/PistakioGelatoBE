package kevindonati.PistakioGelatoBE.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kevindonati.PistakioGelatoBE.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Settings", description = "Application settings management")
@RestController
@RequestMapping("/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping("/shipping-cost")
    public double getShippingCost() {
        return settingsService.getShippingCost();
    }

    @GetMapping("/shipping")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public SettingsService.ShippingSettings getShippingSettings() {
        return settingsService.getShippingSettings();
    }

    @PutMapping("/shipping")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public SettingsService.ShippingSettings updateShippingSettings(@RequestBody ShippingSettingsRequest request) {
        settingsService.updateShippingSettings(
                request.weight1(),
                request.cost1(),
                request.weight2(),
                request.cost2(),
                request.weight3(),
                request.cost3(),
                request.costOver()
        );
        return settingsService.getShippingSettings();
    }

    @GetMapping("/maintenance")
    public boolean getMaintenanceMode() {
        return settingsService.isMaintenanceMode();
    }

    @PutMapping("/maintenance")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean updateMaintenanceMode(@RequestBody MaintenanceModeRequest request) {
        settingsService.updateMaintenanceMode(request.enabled());

        return settingsService.isMaintenanceMode();
    }

    public record ShippingSettingsRequest(
            double weight1,
            double cost1,
            double weight2,
            double cost2,
            double weight3,
            double cost3,
            double costOver
    ) {
    }

    public record MaintenanceModeRequest(
            boolean enabled
    ) {
    }
}