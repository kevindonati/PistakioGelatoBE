package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Tub;
import kevindonati.PistakioGelatoBE.entities.TubTranslation;
import kevindonati.PistakioGelatoBE.enums.Language;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.payloads.TubDTO;
import kevindonati.PistakioGelatoBE.payloads.TubDetailsDTO;
import kevindonati.PistakioGelatoBE.payloads.TubTranslationDTO;
import kevindonati.PistakioGelatoBE.repositories.TubRepository;
import kevindonati.PistakioGelatoBE.repositories.TubTranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class TubService {
    @Autowired
    private TubRepository tubRepository;

    @Autowired
    private TubTranslationRepository tubTranslationRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public Tub save(TubDTO payload, MultipartFile file) {
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(file);
        }

        Tub newTub = new Tub(
                payload.weight(),
                payload.price(),
                imageUrl,
                payload.available()
        );
        Tub savedTub = tubRepository.save(newTub);

        for (TubTranslationDTO translation : payload.translations()) {
            TubTranslation newTranslation =
                    new TubTranslation(
                            translation.language(),
                            translation.name(),
                            translation.description(),
                            savedTub
                    );
            tubTranslationRepository.save(newTranslation);
        }
        return savedTub;
    }

    public Page<TubDetailsDTO> findAll(int page, int size, String orderBy, Language language) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        return tubRepository.findAll(pageable).map(tub -> {
            TubTranslation translation = tubTranslationRepository.findByTubAndLanguage(tub, language)
                    .orElseThrow(() -> new NotFoundException("Translation not found for tub " + tub.getId() + " and language " + language));

            return new TubDetailsDTO(
                    tub.getId(),
                    translation.getName(),
                    translation.getDescription(),
                    tub.getWeight(),
                    tub.getPrice(),
                    tub.getImage(),
                    tub.isAvailable()
            );
        });
    }

    public Tub findById(UUID id) {
        return tubRepository.findById(id).orElseThrow(() -> new NotFoundException("Tub with id " + id + " not found"));
    }

    public TubDetailsDTO findById(UUID id, Language language) {
        Tub tub = findById(id);
        TubTranslation translation = tubTranslationRepository.findByTubAndLanguage(tub, language)
                .orElseThrow(() -> new NotFoundException("Translation not found for tub " + id + " and language " + language));

        return new TubDetailsDTO(
                tub.getId(),
                translation.getName(),
                translation.getDescription(),
                tub.getWeight(),
                tub.getPrice(),
                tub.getImage(),
                tub.isAvailable()
        );
    }

    public Tub findByIdAndUpdate(UUID id, TubDTO payload, MultipartFile file) {
        Tub foundedTub = this.findById(id);

        foundedTub.setWeight(payload.weight());
        foundedTub.setPrice(payload.price());
        foundedTub.setAvailable(payload.available());

        if (file != null && !file.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(file);
            foundedTub.setImage(imageUrl);
        }

        Tub savedTub = tubRepository.save(foundedTub);

        for (TubTranslationDTO translation : payload.translations()) {
            TubTranslation existingTranslation = tubTranslationRepository.findByTubAndLanguage(savedTub, translation.language()).orElse(null);

            if (existingTranslation != null) {
                existingTranslation.setName(translation.name());
                existingTranslation.setDescription(translation.description());

                tubTranslationRepository.save(existingTranslation);
            } else {
                TubTranslation newTranslation = new TubTranslation(
                        translation.language(),
                        translation.name(),
                        translation.description(),
                        savedTub
                );
                tubTranslationRepository.save(newTranslation);
            }
        }
        return savedTub;
    }

    public void findByIdAndDelete(UUID id) {
        Tub foundedTub = this.findById(id);
        tubRepository.delete(foundedTub);
    }
}
