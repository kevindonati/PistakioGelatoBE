package kevindonati.PistakioGelatoBE.controllers;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TubResponseDTO save(@RequestBody @Validated TubDTO payload) {
        Tub savedTub = tubService.save(payload);
        return new TubResponseDTO(savedTub.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Tub findByIdAndUpdate(@PathVariable UUID id, @RequestBody @Validated TubDTO payload) {
        return tubService.findByIdAndUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void findByIdAndDelete(@PathVariable UUID id) {
        tubService.findByIdAndDelete(id);
    }
}
