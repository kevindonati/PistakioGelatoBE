package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.Shipment;
import kevindonati.PistakioGelatoBE.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("kevindonati5@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Benvenuto su PistakioGelato");
        message.setText(
                "Ciao " + user.getName() + "!\n\n" +
                        "Benvenuto su PistakioGelato \n\n" +
                        "Il tuo account è stato creato con successo.\n\n" +
                        "Buon gelato!"
        );
        mailSender.send(message);
    }

    public void sendPaymentCompleteEmail(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("kevindonati5@gmail.com");
        message.setTo(order.getUser().getEmail());
        message.setSubject("Pagamento ricevuto - PistakioGelato");
        message.setText(
                "Ciao " + order.getUser().getName() + "!" +
                        "Abbiamo ricevuto il pagamento del tuo ordine.\n" +
                        "Ordine: " + order.getId() + "\n" +
                        "Totale: €" + order.getTotal() + "\n" +
                        "Il tuo ordine è stato preso in carico.\n" +
                        "Grazie per aver scelto PistakioGelato!"
        );
        mailSender.send(message);
    }

    public void sendShippingEmail(Shipment shipment) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("kevindonati5@gmail.com");
        message.setTo(shipment.getOrder().getUser().getEmail());
        message.setSubject("Il tuo ordine è stato spedito - PistakioGelato");

        message.setText(
                "Ciao " + shipment.getOrder().getUser().getName() + "!\n" +
                        "Il tuo ordine è stato spedito!\n" +
                        "Ordine: " + shipment.getOrder().getId() + "\n" +
                        "Corriere: " + shipment.getCarrier() + "\n" +
                        "Tracking number: " + shipment.getTrackingNumber() + "\n" +
                        "Grazie per aver scelto PistakioGelato!"
        );
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
