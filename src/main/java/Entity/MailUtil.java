package Entity;

import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {
    public static void sendMailAcceptation(String mail) {
        System.out.println("Preparing to send email");

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        String myAccountEmail = "rwafinouba7021@gmail.com";
        String password = "123haha123";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, password);
            }
        });

        try {
            Message message = prepareMessage(session, myAccountEmail, myAccountEmail, "Vous êtes parmi nos Top Personnels 2024 ", "Félicitations Top Personnel");
            Transport.send(message);
            System.out.println("Message sent successfully");
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public static void sendMailRefus(String mail) {
        System.out.println("Preparing to send email");

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        String myAccountEmail = "rwafinouba7021@gmail.com";
        String password = "123haha123";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, password);
            }
        });

        try {
            Message message = prepareMessage(session, myAccountEmail, myAccountEmail, "Votre félicitations été rejeté tant que vous êtes plus parmi nos top personnels .", "Félicitations rejeté");
            Transport.send(message);
            System.out.println("Message sent successfully");
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    private static Message prepareMessage(Session session, String myAccountEmail, String recepient, String messageText, String messageSubject) {
        Message message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
            message.setSubject(messageSubject);
            message.setText(messageText);
        } catch (Exception e) {
            Logger.getLogger(MailUtil.class.getName(), e.getMessage());
        }
        return message;
    }
}
