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
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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
                                            @RequestParam(defaultValue = "EN") Language language) {
        return categoryService.findAll(page, size, orderBy, language);
    }

    @GetMapping("/{id}")
    public CategoryDetailsDTO findById(@PathVariable UUID id, @RequestParam(defaultValue = "EN") Language language) {
        return categoryService.findById(id, language);
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponseDTO save(@RequestPart("data") @Validated CategoryDTO payload, @RequestPart(value = "file", required = false) MultipartFile file) {
        Category savedCategory = categoryService.save(payload, file);

        return new CategoryResponseDTO(savedCategory.getId());
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public Category findByIdAndUpdate(
            @PathVariable UUID id,
            @RequestPart("data")
            @Validated CategoryDTO payload,
            @RequestPart(
                    value = "file",
                    required = false
            )
            MultipartFile file
    ) {
        return categoryService.findByIdAndUpdate(
                id,
                payload,
                file
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public void findByIdAndDelete(
            @PathVariable UUID id
    ) {

        categoryService.findByIdAndDelete(id);
    }
}