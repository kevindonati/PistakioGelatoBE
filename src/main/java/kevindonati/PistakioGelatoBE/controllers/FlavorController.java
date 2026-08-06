package kevindonati.PistakioGelatoBE.controllers;

import kevindonati.PistakioGelatoBE.entities.Flavor;
import kevindonati.PistakioGelatoBE.payloads.FlavorDTO;
import kevindonati.PistakioGelatoBE.payloads.FlavorResponseDTO;
import kevindonati.PistakioGelatoBE.services.FlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/flavors")
public class FlavorController {
    @Autowired
    private FlavorService flavorService;

    @GetMapping
    public Page<Flavor> findAll(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "name") String orderBy
    ) {
        return flavorService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    public Flavor findById(@PathVariable UUID id) {
        return flavorService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlavorResponseDTO save(@RequestBody @Validated FlavorDTO payload) {
        Flavor savedFlavor = flavorService.save(payload);
        return new FlavorResponseDTO(savedFlavor.getId());
    }

    @PutMapping("/{id}")
    public Flavor findByIdAndUpdate(@PathVariable UUID id, @RequestBody @Validated FlavorDTO payload) {
        return flavorService.findByIdAndUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findByIdAndDelete(@PathVariable UUID id) {
        flavorService.findByIdAndDelete(id);
    }

    @GetMapping("/available")
    public List<Flavor> findAvailable() {
        return flavorService.findAvailable();
    }

    @GetMapping("/vegan")
    public List<Flavor> findVegan() {
        return flavorService.findVegan();
    }

    @GetMapping("/gluten-free")
    public List<Flavor> findGlutenFree() {
        return flavorService.findGlutenFree();
    }

    @GetMapping("/lactose-free")
    public List<Flavor> findLactoseFree() {
        return flavorService.findLactoseFree();
    }

    @GetMapping("/sugar-free")
    public List<Flavor> findSugarFree() {
        return flavorService.findSugarFree();
    }
}
