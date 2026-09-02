package kevindonati.PistakioGelatoBE.services;

import jakarta.transaction.Transactional;
import kevindonati.PistakioGelatoBE.entities.Category;
import kevindonati.PistakioGelatoBE.entities.CategoryTranslation;
import kevindonati.PistakioGelatoBE.enums.Language;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.payloads.CategoryDTO;
import kevindonati.PistakioGelatoBE.payloads.CategoryDetailsDTO;
import kevindonati.PistakioGelatoBE.payloads.CategoryTranslationDTO;
import kevindonati.PistakioGelatoBE.repositories.CategoryRepository;
import kevindonati.PistakioGelatoBE.repositories.CategoryTranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryTranslationRepository categoryTranslationRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public Category save(CategoryDTO payload, MultipartFile file) {
        String imageUrl = null;

        if (file != null && !file.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(file);
        }

        Category newCategory = new Category(imageUrl);
        Category savedCategory = categoryRepository.save(newCategory);

        for (CategoryTranslationDTO translation : payload.translations()) {
            if (categoryTranslationRepository.existsByCategoryAndLanguage(savedCategory, translation.language())) {
                throw new BadRequestException("A translation for language " + translation.language() + " already exists");
            }

            CategoryTranslation newTranslation = new CategoryTranslation(
                    translation.language(),
                    translation.name(),
                    translation.description(),
                    savedCategory
            );
            categoryTranslationRepository.save(newTranslation);
        }
        return savedCategory;
    }

    public Page<CategoryDetailsDTO> findAll(int page, int size, String orderBy, Language language) {
        if (size > 20) size = 20;
        if (size < 1) size = 10;
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        return categoryRepository.findAll(pageable).map(category -> {

            CategoryTranslation translation = categoryTranslationRepository.findByCategoryAndLanguage(category, language)
                    .orElseThrow(() -> new NotFoundException("Translation not found for category " + category.getId() + " and language " + language));

            return new CategoryDetailsDTO(
                    category.getId(),
                    translation.getName(),
                    translation.getDescription(),
                    category.getImage()
            );
        });
    }

    public Category findById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
    }

    public CategoryDetailsDTO findById(UUID id, Language language) {
        Category category = findById(id);

        CategoryTranslation translation = categoryTranslationRepository.findByCategoryAndLanguage(category, language)
                .orElseThrow(() -> new NotFoundException("Translation not found for category " + id + " and language " + language));

        return new CategoryDetailsDTO(
                category.getId(),
                translation.getName(),
                translation.getDescription(),
                category.getImage()
        );
    }

    public Category findByIdAndUpdate(UUID id, CategoryDTO payload, MultipartFile file) {
        Category foundedCategory = this.findById(id);

        if (file != null && !file.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(file);
            foundedCategory.setImage(imageUrl);
        }

        Category savedCategory = categoryRepository.save(foundedCategory);

        for (CategoryTranslationDTO translation : payload.translations()) {
            CategoryTranslation existingTranslation = categoryTranslationRepository.findByCategoryAndLanguage(savedCategory, translation.language())
                    .orElse(null);

            if (existingTranslation != null) {
                existingTranslation.setName(
                        translation.name()
                );

                existingTranslation.setDescription(
                        translation.description()
                );

                categoryTranslationRepository.save(
                        existingTranslation
                );
            } else {
                CategoryTranslation newTranslation =
                        new CategoryTranslation(
                                translation.language(),
                                translation.name(),
                                translation.description(),
                                savedCategory
                        );
                categoryTranslationRepository.save(
                        newTranslation
                );
            }
        }
        return savedCategory;
    }

    @Transactional
    public void findByIdAndDelete(UUID id) {
        Category foundedCategory = this.findById(id);
        categoryTranslationRepository.deleteByCategory(foundedCategory);
        categoryRepository.delete(foundedCategory);
    }
}