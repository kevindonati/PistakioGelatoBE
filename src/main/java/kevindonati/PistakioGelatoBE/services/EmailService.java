package kevindonati.PistakioGelatoBE.services;

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
}
