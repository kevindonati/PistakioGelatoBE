package kevindonati.PistakioGelatoBE.controllers;

import kevindonati.PistakioGelatoBE.entities.Flavor;
import kevindonati.PistakioGelatoBE.enums.Language;
import kevindonati.PistakioGelatoBE.payloads.FlavorDTO;
import kevindonati.PistakioGelatoBE.payloads.FlavorDetailsDTO;
import kevindonati.PistakioGelatoBE.payloads.FlavorResponseDTO;
import kevindonati.PistakioGelatoBE.services.FlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/flavors")
public class FlavorController {
    @Autowired
    private FlavorService flavorService;

    @GetMapping("/available")
    public List<FlavorDetailsDTO> findAvailable(@RequestParam(defaultValue = "EN") Language language) {
        return flavorService.findAvailable(language);
    }

    @GetMapping("/vegan")
    public List<FlavorDetailsDTO> findVegan(@RequestParam(defaultValue = "EN") Language language) {
        return flavorService.findVegan(language);
    }

    @GetMapping("/gluten-free")
    public List<FlavorDetailsDTO> findGlutenFree(@RequestParam(defaultValue = "EN") Language language) {
        return flavorService.findGlutenFree(language);
    }

    @GetMapping("/lactose-free")
    public List<FlavorDetailsDTO> findLactoseFree(@RequestParam(defaultValue = "EN") Language language) {
        return flavorService.findLactoseFree(language);
    }

    @GetMapping("/sugar-free")
    public List<FlavorDetailsDTO> findSugarFree(@RequestParam(defaultValue = "EN") Language language) {
        return flavorService.findSugarFree(language);
    }

    @GetMapping
    public Page<FlavorDetailsDTO> findAll(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "referralCode") String orderBy,
                                          @RequestParam(defaultValue = "EN") Language language
    ) {
        return flavorService.findAll(page, size, orderBy, language);
    }

    @GetMapping("/{id}")
    public FlavorDetailsDTO findById(@PathVariable UUID id,
                                     @RequestParam(defaultValue = "EN") Language language
    ) {
        return flavorService.findById(id, language);
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public FlavorResponseDTO save(
            @RequestPart("data") @Validated FlavorDTO payload,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        Flavor savedFlavor = flavorService.save(payload, file);
        return new FlavorResponseDTO(savedFlavor.getId());
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public Flavor findByIdAndUpdate(
            @PathVariable UUID id,
            @RequestPart("data") @Validated FlavorDTO payload,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return flavorService.findByIdAndUpdate(id, payload, file);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void findByIdAndDelete(@PathVariable UUID id) {
        flavorService.findByIdAndDelete(id);
    }
}
