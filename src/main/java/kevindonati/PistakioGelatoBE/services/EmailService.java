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
        message.setSubject("Ordine consegnato - PistakioGelato");

        message.setText(
                "Ciao " + shipment.getOrder().getUser().getName() + "!\n" +
                        "Il tuo ordine è stato consegnato!\n" +
                        "Ordine: " + shipment.getOrder().getId() + "\n" +
                        "Corriere: " + shipment.getCarrier() + "\n" +
                        "Tracking number: " + shipment.getTrackingNumber() + "\n" +
                        "Grazie per aver scelto PistakioGelato!"
        );
        mailSender.send(message);
    }

    public void sendCancellationEmail(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("kevindonati5@gmail.com");
        message.setTo(order.getUser().getEmail());
        message.setSubject("Ordine cancellato - PistakioGelato");

        message.setText(
                "Ciao " + order.getUser().getName() + "!\n" +
                        "Il tuo ordine è stato cancellato.\n" +
                        "Ordine: " + order.getId() + "\n" +
                        "Totale: €" + order.getTotal() + "\n" +
                        "Se non hai richiesto tu la cancellazione, contattaci.\n" +
                        "PistakioGelato"
        );
        mailSender.send(message);
    }
}
