package kevindonati.PistakioGelatoBE.controllers;

import kevindonati.PistakioGelatoBE.entities.Settings;
import kevindonati.PistakioGelatoBE.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
public class SettingsController {
    @Autowired
    private SettingsService settingsService;

    @GetMapping("/shipping-cost")
    public double getShippingCost() {
        return settingsService.getShippingCost();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/shipping-cost")
    public Settings updateShippingCost(@RequestParam double value) {
        return settingsService.updateShippingCost(value);
    }
}
