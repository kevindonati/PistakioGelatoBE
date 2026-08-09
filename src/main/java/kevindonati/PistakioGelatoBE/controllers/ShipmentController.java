package kevindonati.PistakioGelatoBE.controllers;

import kevindonati.PistakioGelatoBE.entities.Shipment;
import kevindonati.PistakioGelatoBE.payloads.ShipmentDTO;
import kevindonati.PistakioGelatoBE.payloads.ShipmentResponseDTO;
import kevindonati.PistakioGelatoBE.services.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {
    @Autowired
    private ShipmentService shipmentService;

    @GetMapping
    public Page<Shipment> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "shippingDate") String orderBy
    ) {
        return shipmentService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    public Shipment findById(@PathVariable UUID id) {
        return shipmentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShipmentResponseDTO save(
            @RequestBody @Validated ShipmentDTO payload
    ) {
        Shipment savedShipment = shipmentService.save(payload);
        return new ShipmentResponseDTO(savedShipment.getId());
    }
}
