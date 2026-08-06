package kevindonati.PistakioGelatoBE.controllers;

import kevindonati.PistakioGelatoBE.entities.Category;
import kevindonati.PistakioGelatoBE.payloads.CategoryDTO;
import kevindonati.PistakioGelatoBE.payloads.CategoryResponseDTO;
import kevindonati.PistakioGelatoBE.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Page<Category> findAll(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "name") String orderBy
    ) {
        return categoryService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    public Category findById(@PathVariable UUID id) {
        return categoryService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDTO save(@RequestBody @Validated CategoryDTO payload) {
        Category savedCategory = categoryService.save(payload);
        return new CategoryResponseDTO(savedCategory.getId());
    }

    @PutMapping("/{id}")
    public Category findByIdAndUpdate(@PathVariable UUID id, @RequestBody @Validated CategoryDTO payload) {
        return categoryService.findByIdAndUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    public void findByIdAndDelete(@PathVariable UUID id) {
        categoryService.findByIdAndDelete(id);
    }
}
