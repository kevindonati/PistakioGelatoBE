package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Category;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.payloads.CategoryDTO;
import kevindonati.PistakioGelatoBE.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category save(CategoryDTO payload) {
        if (categoryRepository.existsByName(payload.name())) {
            throw new BadRequestException("The category with the name " + payload.name() + " is already existing");
        }

        Category newCategory = new Category(payload.name(), payload.description(), payload.image());
        return categoryRepository.save(newCategory);
    }

    public Page<Category> findAll(int page, int size, String orderBy) {
        if (size > 20) size = 20;
        if (size < 1) size = 10;
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        return categoryRepository.findAll(pageable);
    }

    public Category findById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
    }

    public Category findByIdAndUpdate(UUID id, CategoryDTO payload) {
        Category foundedCategory = this.findById(id);

        if (!foundedCategory.getName().equals(payload.name()) && categoryRepository.existsByName(payload.name())) {
            throw new BadRequestException("This category name is already registered");
        }

        foundedCategory.setName(payload.name());
        foundedCategory.setDescription(payload.description());
        foundedCategory.setImage(payload.image());
        return categoryRepository.save(foundedCategory);
    }

    public void findByIdAndDelete(UUID id) {
        Category foundedCategory = this.findById(id);
        categoryRepository.delete(foundedCategory);
    }
}
