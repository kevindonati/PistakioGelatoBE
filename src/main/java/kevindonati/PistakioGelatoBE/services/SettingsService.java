package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Settings;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.repositories.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    @Autowired
    private SettingsRepository settingsRepository;

    public double getShippingCost() {
        Settings setting = settingsRepository.findByName("SHIPPING_COST").orElseThrow(() -> new NotFoundException("Shipping cost not configured"));
        return setting.getValue();
    }

    public Settings updateShippingCost(double value) {
        if (value < 0) {
            throw new BadRequestException("Shipping cost cannot be negative");
        }

        Settings setting = settingsRepository.findByName("SHIPPING_COST").orElseGet(() -> new Settings("SHIPPING_COST", 0));
        setting.setValue(value);
        return settingsRepository.save(setting);
    }
}
