/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common;

import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author jean
 */
public class MailSender {
    private static final Logger LOGGER = Logger.getLogger(MailSender.class.getName());

    public static boolean sendMail(String to, String from, String host, String subject, String msg) {
        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
//        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");        

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(msg);

            // Send message
            Transport.send(message);
            
            return true;
        } catch (MessagingException ex) {
            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    public static boolean sendMailWithAttachement(String from, String to, String cc, String host, String subject, String msg, String[] attachements) {
        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
//        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");        
        // Get the default Session object.
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                try {
                    return new PasswordAuthentication(AppProperties.getInstance().getEmailUID(), AppProperties.getInstance().getEmailPWD());
                } catch (Exception ex) {
                    Logger.getLogger(MailSender.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }
        };
        Session session = Session.getInstance(properties,  authenticator);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            
            // Set CC: header field of the header.
            if(cc !=null && !cc.isEmpty()) {
                for(String rec: cc.split("[;]")) {
                    message.addRecipient(Message.RecipientType.CC,
                        new InternetAddress(rec));
                }
            }
            
            // Set Subject: header field
            message.setSubject(subject);

            // Create the message part 
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(msg);

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);
            
            for (String attachement : attachements) {
            // Part two is attachment
                messageBodyPart = new MimeBodyPart();
                String filename = attachement;
                DataSource source = new FileDataSource(filename);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);
            }

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            return true;
        } catch (MessagingException ex) {
            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }
}
