package main.java.models;

import main.java.configs.EmailConfig;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Email {

    //EmailConfig is a class in main.java.configs package which was NOT committed to GitHub
    //Create your own class and add fields for gmail username and password
    private static final String gmailUser = EmailConfig.GMAIL_USER;
    private static final String gmailPassword = EmailConfig.GMAIL_PASS;

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public static void sendEmail(String address, String subject, String text)
    {
        Runnable r = () -> {
            try {

                Session session = getSession();

                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress("SparkJava Test Server <" + gmailUser + ">"));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(address));
                message.setSubject(subject);
                message.setText(text);

                Transport.send(message);

            } catch (AddressException e) {
                throw new IllegalArgumentException(e);
            } catch (MessagingException e) {
                throw new IllegalArgumentException(e);
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(r);

    }

    public static void sendEmailHTML(String address, String subject, String template)
    {

        Runnable r = () -> {
            try {

                Session session = getSession();

                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress("SparkJava Test Server <" + gmailUser + ">"));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(address));
                message.setSubject(subject);
                message.setContent(template, "text/html; charset=utf-8");

                Transport.send(message);

            } catch (AddressException e) {
                throw new IllegalArgumentException(e);
            } catch (MessagingException e) {
                throw new IllegalArgumentException(e);
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(r);
    }

    private static Session getSession()
    {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties,
        new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(gmailUser, gmailPassword);
            }
        });

        return session;
    }

    public static String getStringFromTemplate(String filename)
    {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("src/main/resources/email_templates/" + filename));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
