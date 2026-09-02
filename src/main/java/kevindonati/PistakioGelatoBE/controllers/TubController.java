package kevindonati.PistakioGelatoBE.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kevindonati.PistakioGelatoBE.entities.Tub;
import kevindonati.PistakioGelatoBE.enums.Language;
import kevindonati.PistakioGelatoBE.payloads.TubDTO;
import kevindonati.PistakioGelatoBE.payloads.TubDetailsDTO;
import kevindonati.PistakioGelatoBE.payloads.TubResponseDTO;
import kevindonati.PistakioGelatoBE.services.TubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/tubs")
public class TubController {
    @Autowired
    private TubService tubService;

    @GetMapping
    public Page<TubDetailsDTO> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "weight") String orderBy,
            @RequestParam(defaultValue = "EN") Language language
    ) {
        return tubService.findAll(page, size, orderBy, language);
    }

    @GetMapping("/{id}")
    public TubDetailsDTO findById(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "EN") Language language
    ) {
        return tubService.findById(id, language);
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public TubResponseDTO save(
            @RequestPart("data") @Validated TubDTO payload,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        Tub savedTub = tubService.save(payload, file);
        return new TubResponseDTO(savedTub.getId());
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public Tub findByIdAndUpdate(
            @PathVariable UUID id,
            @RequestPart("data") @Validated TubDTO payload,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return tubService.findByIdAndUpdate(id, payload, file);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public void findByIdAndDelete(@PathVariable UUID id) {
        tubService.findByIdAndDelete(id);
    }
}
