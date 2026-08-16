package kevindonati.PistakioGelatoBE.controllers;

import kevindonati.PistakioGelatoBE.entities.Category;
import kevindonati.PistakioGelatoBE.enums.Language;
import kevindonati.PistakioGelatoBE.payloads.CategoryDTO;
import kevindonati.PistakioGelatoBE.payloads.CategoryDetailsDTO;
import kevindonati.PistakioGelatoBE.payloads.CategoryResponseDTO;
import kevindonati.PistakioGelatoBE.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Page<CategoryDetailsDTO> findAll(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(defaultValue = "image") String orderBy,
                                            @RequestParam(defaultValue = "EN") Language language
    ) {
        return categoryService.findAll(
                page,
                size,
                orderBy,
                language
        );
    }

    @GetMapping("/{id}")
    public CategoryDetailsDTO findById(@PathVariable UUID id,
                                       @RequestParam(defaultValue = "EN") Language language
    ) {
        return categoryService.findById(id, language);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponseDTO save(@RequestBody @Validated CategoryDTO payload) {
        Category savedCategory = categoryService.save(payload);
        return new CategoryResponseDTO(savedCategory.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Category findByIdAndUpdate(@PathVariable UUID id, @RequestBody @Validated CategoryDTO payload) {
        return categoryService.findByIdAndUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void findByIdAndDelete(@PathVariable UUID id) {
        categoryService.findByIdAndDelete(id);
    }
}
