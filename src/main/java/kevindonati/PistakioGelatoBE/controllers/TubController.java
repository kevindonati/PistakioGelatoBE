package kevindonati.PistakioGelatoBE.controllers;

import kevindonati.PistakioGelatoBE.entities.Tub;
import kevindonati.PistakioGelatoBE.payloads.TubDTO;
import kevindonati.PistakioGelatoBE.payloads.TubResponseDTO;
import kevindonati.PistakioGelatoBE.services.TubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tubs")
public class TubController {
    @Autowired
    private TubService tubService;

    @GetMapping
    public Page<Tub> findAll(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(defaultValue = "name") String orderBy
    ) {
        return tubService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    public Tub findById(@PathVariable UUID id) {
        return tubService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TubResponseDTO save(@RequestBody @Validated TubDTO payload) {
        Tub savedTub = tubService.save(payload);
        return new TubResponseDTO(savedTub.getId());
    }

    @PutMapping("/{id}")
    public Tub findByIdAndUpdate(@PathVariable UUID id, @RequestBody @Validated TubDTO payload) {
        return tubService.findByIdAndUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findByIdAndDelete(@PathVariable UUID id) {
        tubService.findByIdAndDelete(id);
    }
}
