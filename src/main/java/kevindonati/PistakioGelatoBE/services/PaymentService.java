package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.Payment;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.enums.PaymentStatus;
import kevindonati.PistakioGelatoBE.enums.UserRole;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EmailService emailService;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public Page<Payment> findAll(int page, int size, String orderBy) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        User authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getRole() == UserRole.ADMIN) {
            return paymentRepository.findAll(pageable);
        }
        return paymentRepository.findByOrderUserId(authenticatedUser.getId(), pageable);
    }

    public Payment findById(UUID id) {
        Payment foundedPayment = paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Payment with id " + id + " not found"));
        orderService.findById(foundedPayment.getOrder().getId());
        return foundedPayment;
    }

    @Transactional
    public Payment completeStripePayment(String transactionId, String eventId) {
        Optional<Payment> alreadyProcessed = paymentRepository.findByStripeEventId(eventId);
        if (alreadyProcessed.isPresent()) {
            return alreadyProcessed.get();
        }

        Payment foundedPayment = paymentRepository.findByIdTransaction(transactionId).orElseThrow(() -> new NotFoundException("Payment with transaction " + transactionId + " not found"));

        if (foundedPayment.getStatus() == PaymentStatus.COMPLETED) {
            return foundedPayment;
        }

        foundedPayment.setStatus(PaymentStatus.COMPLETED);
        foundedPayment.setStripeEventId(eventId);

        Order order = orderService.confirmPaymentFromStripe(foundedPayment.getOrder().getId());
        orderService.decreaseStock(order);
        emailService.sendPaymentCompleteEmail(order);

        return paymentRepository.save(foundedPayment);
    }
}
