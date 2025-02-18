package al.algorthyhm.utils;


import al.algorthyhm.service.ExcelService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailUtils {
    public  String username ;
    public  String password;
    private static EmailUtils instance;
    private final static Logger logger = LogManager.getLogger(ExcelService.class);


    private EmailUtils(){}

    public static synchronized EmailUtils getInstance() {
        if (instance == null) {
            instance = new EmailUtils();
        }
        return instance;
    }

    public void sendEmail(List<InternetAddress> emailList, String compay, String emri) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.submit(() -> {
            try {
                send(emailList,compay,emri);
                Thread.sleep(50000);
            } catch (MessagingException e) {
                logger.info( "Failed to send email", e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        executor.shutdown();
    }

    public void send(List<InternetAddress> emailList,  String compay, String emri) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session;
        try {
            session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } catch (Exception e) {
            logger.info( "Failed to create session", e);
            throw new MessagingException("Failed to create session", e);
        }

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.BCC, emailList.toArray(new InternetAddress[0]));

        message.setSubject("Test Email");
        message.setText("Good Morning "+ emri
                +  ",        \nHope everything is going well for you.\n"
                +  "As " + compay +" is an experienced......");
        try {
            Transport.send(message);
            logger.info("Email send..");
        } catch (Exception e) {
            logger.info(  "Failed to send email", e);
            throw new MessagingException("Failed to send email", e);
        }
    }

    public void sendEmail(List<InternetAddress> recipientAddresses) {
    }
}
