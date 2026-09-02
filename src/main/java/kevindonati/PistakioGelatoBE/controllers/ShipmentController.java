package kevindonati.PistakioGelatoBE.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kevindonati.PistakioGelatoBE.entities.Shipment;
import kevindonati.PistakioGelatoBE.enums.ShipmentStatus;
import kevindonati.PistakioGelatoBE.payloads.ShipmentDTO;
import kevindonati.PistakioGelatoBE.payloads.ShipmentResponseDTO;
import kevindonati.PistakioGelatoBE.services.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Shipments", description = "Shipment management")
@RestController
@RequestMapping("/shipments")
public class ShipmentController {
    @Autowired
    private ShipmentService shipmentService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Page<Shipment> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "shippingDate") String orderBy
    ) {
        return shipmentService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public Shipment findById(@PathVariable UUID id) {
        return shipmentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ShipmentResponseDTO save(
            @RequestBody @Validated ShipmentDTO payload
    ) {
        Shipment savedShipment = shipmentService.save(payload);
        return new ShipmentResponseDTO(savedShipment.getId());
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public Shipment updateStatus(
            @PathVariable UUID id,
            @RequestParam ShipmentStatus status
    ) {
        return shipmentService.updateStatus(id, status);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findByIdAndDelete(@PathVariable UUID id) {
        shipmentService.findByIdAndDelete(id);
    }
}
