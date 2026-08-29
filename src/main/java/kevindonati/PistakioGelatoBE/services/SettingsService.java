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

    private static final String SHIPPING_WEIGHT_1 = "SHIPPING_WEIGHT_1";
    private static final String SHIPPING_COST_1 = "SHIPPING_COST_1";

    private static final String SHIPPING_WEIGHT_2 = "SHIPPING_WEIGHT_2";
    private static final String SHIPPING_COST_2 = "SHIPPING_COST_2";

    private static final String SHIPPING_WEIGHT_3 = "SHIPPING_WEIGHT_3";
    private static final String SHIPPING_COST_3 = "SHIPPING_COST_3";

    private static final String SHIPPING_COST_OVER = "SHIPPING_COST_OVER";

    public double getShippingCost() {
        return getSettingValue(SHIPPING_COST_1);
    }

    public double getShippingCost(double totalWeightKg) {

        double weight1 = getSettingValue(SHIPPING_WEIGHT_1);
        double cost1 = getSettingValue(SHIPPING_COST_1);

        double weight2 = getSettingValue(SHIPPING_WEIGHT_2);
        double cost2 = getSettingValue(SHIPPING_COST_2);

        double weight3 = getSettingValue(SHIPPING_WEIGHT_3);
        double cost3 = getSettingValue(SHIPPING_COST_3);

        double costOver = getSettingValue(SHIPPING_COST_OVER);

        if (totalWeightKg <= weight1) {
            return cost1;
        }

        if (totalWeightKg <= weight2) {
            return cost2;
        }

        if (totalWeightKg <= weight3) {
            return cost3;
        }

        return costOver;
    }

    public Settings updateShippingCost(double value) {
        if (value < 0) {
            throw new BadRequestException("Shipping cost cannot be negative");
        }

        return updateSetting(SHIPPING_COST_1, value);
    }

    public void updateShippingSettings(double weight1, double cost1, double weight2, double cost2, double weight3, double cost3, double costOver) {
        if (weight1 <= 0 || weight2 <= 0 || weight3 <= 0) {
            throw new BadRequestException("Shipping weights must be greater than zero");
        }

        if (weight1 >= weight2 || weight2 >= weight3) {
            throw new BadRequestException("Shipping weights must be in ascending order");
        }

        if (cost1 < 0 || cost2 < 0 || cost3 < 0 || costOver < 0) {
            throw new BadRequestException("Shipping costs cannot be negative");
        }

        updateSetting(SHIPPING_WEIGHT_1, weight1);
        updateSetting(SHIPPING_COST_1, cost1);
        updateSetting(SHIPPING_WEIGHT_2, weight2);
        updateSetting(SHIPPING_COST_2, cost2);
        updateSetting(SHIPPING_WEIGHT_3, weight3);
        updateSetting(SHIPPING_COST_3, cost3);
        updateSetting(SHIPPING_COST_OVER, costOver);
    }

    public ShippingSettings getShippingSettings() {
        return new ShippingSettings(
                getSettingValue(SHIPPING_WEIGHT_1),
                getSettingValue(SHIPPING_COST_1),
                getSettingValue(SHIPPING_WEIGHT_2),
                getSettingValue(SHIPPING_COST_2),
                getSettingValue(SHIPPING_WEIGHT_3),
                getSettingValue(SHIPPING_COST_3),
                getSettingValue(SHIPPING_COST_OVER)
        );
    }

    private double getSettingValue(String name) {
        return settingsRepository
                .findByName(name)
                .orElseThrow(() -> new NotFoundException("Setting " + name + " not configured"))
                .getValue();
    }

    private Settings updateSetting(String name, double value) {
        Settings setting = settingsRepository
                .findByName(name)
                .orElseGet(() -> new Settings(name, value));
        setting.setValue(value);
        return settingsRepository.save(setting);
    }

    public record ShippingSettings(
            double weight1,
            double cost1,
            double weight2,
            double cost2,
            double weight3,
            double cost3,
            double costOver
    ) {
    }
}