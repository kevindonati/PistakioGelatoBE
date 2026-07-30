package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Category;
import kevindonati.PistakioGelatoBE.entities.Flavor;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.payloads.FlavorDTO;
import kevindonati.PistakioGelatoBE.repositories.CategoryRepository;
import kevindonati.PistakioGelatoBE.repositories.FlavorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FlavorService {
    @Autowired
    private FlavorRepository flavorRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public Flavor save(FlavorDTO payload) {
        if (flavorRepository.existsByName(payload.name())) {
            throw new BadRequestException("The flavor with the name " + payload.name() + " is already existing");
        }

        Category foundedCategory = categoryRepository.findById(payload.category()).orElseThrow(() -> new NotFoundException("Category with id " + payload.category() + " not found"));
        Flavor newFlavor = new Flavor(payload.name(),
                payload.description(),
                payload.referralCode(),
                payload.image(),
                payload.stockPortions(),
                payload.available(),
                payload.vegan(),
                payload.lactoseFree(),
                payload.glutenFree(),
                payload.sugarFree(),
                foundedCategory);

        return flavorRepository.save(newFlavor);
    }

    public Page<Flavor> findAll(int page, int size, String orderBy) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        return flavorRepository.findAll(pageable);
    }

    public Flavor findById(UUID id) {
        return flavorRepository.findById(id).orElseThrow(() -> new NotFoundException("Flavor with id " + id + " not found"));
    }

    public Flavor findByIdAndUpdate(UUID id, FlavorDTO payload) {
        Flavor foundedFlavor = this.findById(id);
        if (!foundedFlavor.getName().equals(payload.name()) && flavorRepository.existsByName(payload.name())) {
            throw new BadRequestException("This flavor name is already registered");
        }

        Category foundedCategory = categoryRepository.findById(payload.category()).orElseThrow(() -> new NotFoundException("Category with id " + payload.category() + " not found"));

        foundedFlavor.setName(payload.name());
        foundedFlavor.setDescription(payload.description());
        foundedFlavor.setReferralCode(payload.referralCode());
        foundedFlavor.setImage(payload.image());
        foundedFlavor.setStockPortions(payload.stockPortions());
        foundedFlavor.setAvailable(payload.available());
        foundedFlavor.setVegan(payload.vegan());
        foundedFlavor.setLactoseFree(payload.lactoseFree());
        foundedFlavor.setGlutenFree(payload.glutenFree());
        foundedFlavor.setSugarFree(payload.sugarFree());
        foundedFlavor.setCategory(foundedCategory);

        return flavorRepository.save(foundedFlavor);
    }

    public void findByIdAndDelete(UUID id) {
        Flavor foundedFlavor = this.findById(id);
        flavorRepository.delete(foundedFlavor);
    }
}
