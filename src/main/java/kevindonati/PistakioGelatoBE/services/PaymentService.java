package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.Payment;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import kevindonati.PistakioGelatoBE.enums.PaymentStatus;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.payloads.PaymentDTO;
import kevindonati.PistakioGelatoBE.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    public Payment save(PaymentDTO payload) {
        Order foundedOrder = orderService.findById(payload.order());
        if (foundedOrder.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BadRequestException("The order is not waiting for payment");
        }

        if (foundedOrder.getTotal() <= 0) {
            throw new BadRequestException("The order total must be greater than zero");
        }

        if (paymentRepository.existsByOrder(foundedOrder)) {
            throw new BadRequestException("This order already has a payment");
        }

        Payment newPayment = new Payment(
                payload.providerType(),
                UUID.randomUUID().toString(),
                foundedOrder.getTotal(),
                "EUR",
                PaymentStatus.PENDING,
                foundedOrder
        );
        return paymentRepository.save(newPayment);
    }

    public Page<Payment> findAll(int page, int size, String orderBy) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        return paymentRepository.findAll(pageable);
    }

    public Payment findById(UUID id) {
        return paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Payment with id " + id + " not found"));
    }
}
