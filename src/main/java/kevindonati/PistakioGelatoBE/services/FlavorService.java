package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Category;
import kevindonati.PistakioGelatoBE.entities.Flavor;
import kevindonati.PistakioGelatoBE.entities.FlavorTranslation;
import kevindonati.PistakioGelatoBE.enums.Language;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.payloads.FlavorDTO;
import kevindonati.PistakioGelatoBE.payloads.FlavorDetailsDTO;
import kevindonati.PistakioGelatoBE.payloads.FlavorResponseDTO;
import kevindonati.PistakioGelatoBE.payloads.FlavorTranslationDTO;
import kevindonati.PistakioGelatoBE.repositories.CategoryRepository;
import kevindonati.PistakioGelatoBE.repositories.FlavorRepository;
import kevindonati.PistakioGelatoBE.repositories.FlavorTranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class FlavorService {
    @Autowired
    private FlavorRepository flavorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FlavorTranslationRepository flavorTranslationRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    private FlavorDetailsDTO toDetailsDTO(Flavor flavor, Language language) {
        FlavorTranslation translation = flavorTranslationRepository.findByFlavorAndLanguage(flavor, language)
                .orElseThrow(() -> new NotFoundException("Translation not found for flavor " + flavor.getId() + " and language " + language));

        return new FlavorDetailsDTO(
                flavor.getId(),
                translation.getName(),
                translation.getDescription(),
                flavor.getReferralCode(),
                flavor.getImage(),
                flavor.getStockPortions(),
                flavor.isAvailable(),
                flavor.isVegan(),
                flavor.isLactoseFree(),
                flavor.isGlutenFree(),
                flavor.isSugarFree(),
                flavor.getCategory().getId()
        );
    }

    public Flavor save(FlavorDTO payload, MultipartFile file) {
        Category foundedCategory = categoryRepository.findById(payload.category()).orElseThrow(() -> new NotFoundException("Category with id " + payload.category() + " not found"));

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(file);
        }

        Flavor newFlavor = new Flavor(
                payload.referralCode(),
                imageUrl,
                payload.stockPortions(),
                payload.available(),
                payload.vegan(),
                payload.lactoseFree(),
                payload.glutenFree(),
                payload.sugarFree(),
                foundedCategory);

        Flavor savedFlavor = flavorRepository.save(newFlavor);

        for (FlavorTranslationDTO translation : payload.translations()) {
            if (flavorTranslationRepository.existsByFlavorAndLanguage(savedFlavor, translation.language())) {
                throw new BadRequestException("A translation for language " + translation.language() + " already exists");
            }

            FlavorTranslation newTranslation =
                    new FlavorTranslation(
                            translation.language(),
                            translation.name(),
                            translation.description(),
                            savedFlavor
                    );
            flavorTranslationRepository.save(newTranslation);
        }
        return savedFlavor;
    }

    public Page<FlavorDetailsDTO> findAll(int page, int size, String orderBy, Language language) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        return flavorRepository.findAll(pageable).map(flavor -> toDetailsDTO(flavor, language));
    }

    public FlavorDetailsDTO findById(UUID id, Language language) {
        Flavor flavor = flavorRepository.findById(id).orElseThrow(() -> new NotFoundException("Flavor with id " + id + " not found"));
        return toDetailsDTO(flavor, language);
    }

    public Flavor findFlavorEntityById(UUID id) {
        return flavorRepository.findById(id).orElseThrow(() -> new NotFoundException("Flavor with id " + id + " not found"));
    }

    public Flavor findByIdAndUpdate(UUID id, FlavorDTO payload, MultipartFile file) {
        Flavor foundedFlavor = this.findFlavorEntityById(id);

        Category foundedCategory = categoryRepository.findById(payload.category()).orElseThrow(() -> new NotFoundException("Category with id " + payload.category() + " not found"));

        foundedFlavor.setReferralCode(payload.referralCode());
        if (file != null && !file.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(file);
            foundedFlavor.setImage(imageUrl);
        }
        foundedFlavor.setStockPortions(payload.stockPortions());
        foundedFlavor.setAvailable(payload.available());
        foundedFlavor.setVegan(payload.vegan());
        foundedFlavor.setLactoseFree(payload.lactoseFree());
        foundedFlavor.setGlutenFree(payload.glutenFree());
        foundedFlavor.setSugarFree(payload.sugarFree());
        foundedFlavor.setCategory(foundedCategory);

        Flavor savedFlavor =
                flavorRepository.save(foundedFlavor);

        for (FlavorTranslationDTO translation : payload.translations()) {
            FlavorTranslation existingTranslation = flavorTranslationRepository.findByFlavorAndLanguage(savedFlavor, translation.language()).orElse(null);

            if (existingTranslation != null) {
                existingTranslation.setName(translation.name());
                existingTranslation.setDescription(translation.description());

                flavorTranslationRepository.save(existingTranslation);
            } else {
                FlavorTranslation newTranslation =
                        new FlavorTranslation(
                                translation.language(),
                                translation.name(),
                                translation.description(),
                                savedFlavor
                        );
                flavorTranslationRepository.save(newTranslation);
            }
        }
        return savedFlavor;
    }

    public void findByIdAndDelete(UUID id) {
        Flavor foundedFlavor = this.findFlavorEntityById(id);
        flavorRepository.delete(foundedFlavor);
    }

    public List<FlavorDetailsDTO> findAvailable(Language language) {
        return flavorRepository.findByAvailableTrue()
                .stream()
                .map(flavor -> toDetailsDTO(flavor, language))
                .toList();
    }

    public List<FlavorDetailsDTO> findVegan(Language language) {
        return flavorRepository.findByVeganTrue()
                .stream()
                .map(flavor -> toDetailsDTO(flavor, language))
                .toList();
    }

    public List<FlavorDetailsDTO> findGlutenFree(Language language) {
        return flavorRepository.findByGlutenFreeTrue()
                .stream()
                .map(flavor -> toDetailsDTO(flavor, language))
                .toList();
    }

    public List<FlavorDetailsDTO> findLactoseFree(Language language) {
        return flavorRepository.findByLactoseFreeTrue()
                .stream()
                .map(flavor -> toDetailsDTO(flavor, language))
                .toList();
    }

    public List<FlavorDetailsDTO> findSugarFree(Language language) {
        return flavorRepository.findBySugarFreeTrue()
                .stream()
                .map(flavor -> toDetailsDTO(flavor, language))
                .toList();
    }
}
