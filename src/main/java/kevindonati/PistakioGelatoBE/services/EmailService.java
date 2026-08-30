package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.Shipment;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.enums.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MessageSource messageSource;

    private String getMessage(String key, Language language, Object... args) {
        Locale locale = Locale.forLanguageTag(language.name());

        return messageSource.getMessage(key, args, locale);
    }

    public void sendWelcomeEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("kevindonati5@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject(getMessage("welcome.subject", user.getLanguage()));
        message.setText(getMessage("welcome.text", user.getLanguage(), user.getName()));
        mailSender.send(message);
    }

    public void sendPaymentCompleteEmail(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("kevindonati5@gmail.com");
        message.setTo(order.getUser().getEmail());
        message.setSubject(getMessage("payment.subject", order.getUser().getLanguage()));
        message.setText(getMessage("payment.text", order.getUser().getLanguage(), order.getUser().getName(), order.getId(), order.getTotal()));
        mailSender.send(message);
    }

    public void sendShippingEmail(Shipment shipment) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("kevindonati5@gmail.com");
        message.setTo(shipment.getOrder().getUser().getEmail());
        message.setSubject(getMessage("shipping.subject", shipment.getOrder().getUser().getLanguage()));
        message.setText(getMessage("shipping.text",
                shipment.getOrder().getUser().getLanguage(),
                shipment.getOrder().getUser().getName(),
                shipment.getOrder().getId(),
                shipment.getCarrier(),
                shipment.getTrackingNumber()));
        mailSender.send(message);
    }

    public void sendDeliveryEmail(Shipment shipment) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("kevindonati5@gmail.com");
        message.setTo(shipment.getOrder().getUser().getEmail());
        message.setSubject(getMessage("delivery.subject", shipment.getOrder().getUser().getLanguage()));
        message.setText(getMessage(
                        "delivery.text",
                        shipment.getOrder().getUser().getLanguage(),
                        shipment.getOrder().getUser().getName(),
                        shipment.getOrder().getId(),
                        shipment.getCarrier(),
                        shipment.getTrackingNumber()
                )
        );
        mailSender.send(message);
    }

    public void sendCancellationEmail(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("kevindonati5@gmail.com");
        message.setTo(order.getUser().getEmail());
        message.setSubject(getMessage("cancellation.subject", order.getUser().getLanguage()));
        message.setText(getMessage(
                "cancellation.text",
                order.getUser().getLanguage(),
                order.getUser().getName(),
                order.getId(),
                order.getTotal()
        ));
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(
            User user,
            String resetUrl
    ) {
        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom("kevindonati5@gmail.com");

        message.setTo(user.getEmail());

        message.setSubject(
                getMessage(
                        "password-reset.subject",
                        user.getLanguage()
                )
        );

        message.setText(
                getMessage(
                        "password-reset.text",
                        user.getLanguage(),
                        user.getName(),
                        resetUrl
                )
        );

        mailSender.send(message);
    }
}
