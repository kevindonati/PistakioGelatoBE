package kevindonati.PistakioGelatoBE.controllers;

import kevindonati.PistakioGelatoBE.entities.Payment;
import kevindonati.PistakioGelatoBE.payloads.PaymentDTO;
import kevindonati.PistakioGelatoBE.payloads.PaymentResponseDTO;
import kevindonati.PistakioGelatoBE.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public Page<Payment> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String orderBy
    ) {
        return paymentService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    public Payment findById(@PathVariable UUID id) {
        return paymentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDTO save(
            @RequestBody @Validated PaymentDTO payload
    ) {
        Payment savedPayment = paymentService.save(payload);
        return new PaymentResponseDTO(savedPayment.getId());
    }
}
