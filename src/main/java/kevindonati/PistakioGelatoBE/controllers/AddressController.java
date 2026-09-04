package kevindonati.PistakioGelatoBE.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kevindonati.PistakioGelatoBE.entities.Address;
import kevindonati.PistakioGelatoBE.payloads.AddressDTO;
import kevindonati.PistakioGelatoBE.payloads.AddressResponseDTO;
import kevindonati.PistakioGelatoBE.services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Addresses", description = "User address management")
@RestController
@RequestMapping("/addresses")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @GetMapping
    public Page<Address> findAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "name") String orderBy
    ) {
        return addressService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    public Address findById(@PathVariable UUID id) {
        return addressService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponseDTO save(@RequestBody @Validated AddressDTO payload) {
        Address savedAddress = addressService.save(payload);
        return new AddressResponseDTO(savedAddress.getId());
    }

    @PutMapping("/{id}")
    public Address findByIdAndUpdate(@PathVariable UUID id, @RequestBody @Validated AddressDTO payload) {
        return addressService.findByIdAndUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findByIdAndDelete(@PathVariable UUID id) {
        addressService.findByIdAndDelete(id);
    }
}
