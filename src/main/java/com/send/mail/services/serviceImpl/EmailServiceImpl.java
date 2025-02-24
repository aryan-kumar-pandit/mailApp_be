package com.send.mail.services.serviceImpl;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.send.mail.helper.Message;
import com.send.mail.services.EmailService;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;

    @Value("${spring.mail.properties.domain_name}")
    String mailFrom;

    private Logger logger= LoggerFactory.getLogger(EmailServiceImpl.class);

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String message) {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom(mailFrom);
        mailSender.send(simpleMailMessage);
        logger.info("Email sent to : "+to);

    }

    @Override
    public void sendEmail(String[] to, String subject, String message) {
       SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
       simpleMailMessage.setTo(to);
       simpleMailMessage.setSubject(subject);
       simpleMailMessage.setText(message);
       simpleMailMessage.setFrom(mailFrom);
       mailSender.send(simpleMailMessage);
       logger.info("email sent to All address");

    }

    @Override
    public void sendEmailWithHtml(String to, String subject, String htmlContent) {
        MimeMessage simpleMailMessage=mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper=new MimeMessageHelper(simpleMailMessage,true,"UTF-8");
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setFrom(mailFrom);
            messageHelper.setText(htmlContent,true);
            mailSender.send(simpleMailMessage);
            logger.info("email sent to");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void sendEmailWithFile(String to, String subject, String message, File file) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
            messageHelper.setFrom(mailFrom);
            messageHelper.setTo(to);
            messageHelper.setText(message);
            messageHelper.setSubject(subject);
            FileSystemResource fileSystemResource = new FileSystemResource(file);
            messageHelper.addAttachment(file.getName(), file);
            mailSender.send(mimeMessage);
            logger.info("mail sent with attachment");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void sendEmailWithFile(String to, String subject, String message, InputStream is) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
            messageHelper.setFrom(mailFrom);
            messageHelper.setTo(to);
            messageHelper.setText(message,true);
            messageHelper.setSubject(subject);
            File file=new File("src/main/resources/email/test.png");
            Files.copy(is,file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            FileSystemResource fileSystemResource = new FileSystemResource(file);
            messageHelper.addAttachment(fileSystemResource.getFilename(), file);

            mailSender.send(mimeMessage);
            logger.info("mail sent with attachment");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Value("${mail.store.protocol}")
    String protocol;
    @Value("${mail.imaps.host}")
    String host;
    @Value("${mail.imaps.port}")
    String port;
    @Value("${spring.mail.username}")
    String username;
    @Value("${spring.mail.password}")
    String password;

    @Override
    public List<Message> getInboxMessages() {

        // code to receive email: All emails
        Properties configurations = new Properties();
        configurations.setProperty("mail.store.protocol",protocol);
        configurations.setProperty("mail.imaps.host",host);
        configurations.setProperty("mail.imaps.port",port);
        Session session = Session.getDefaultInstance(configurations);
        try {
            Store store = session.getStore();
            store.connect(username,password);
            //store.getFolder("INBOX"); -- for inbox
            //store.getFolder("[Gmail]/Starred"); -- for starred
            //store.getFolder("[Gmail]/Drafts"); -- for drafts
            Folder inbox = store.getFolder("[Gmail]/Sent Mail");
            inbox.open(Folder.READ_ONLY);
            jakarta.mail.Message[] messages = inbox.getMessages();

            List<Message> list= new ArrayList<>();

            for(jakarta.mail.Message message:messages)
            {
                String content=getContentFromEmailMessage(message);
                List<String> files=getFilesFromEmailMessage(message);

                Message m=new Message();
                m.setContent(content);
                m.setFiles(files);
                m.setSubjects(message.getSubject());
                list.add(m);
            }

        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return List.of();
    }

    private List<String> getFilesFromEmailMessage(jakarta.mail.Message message) throws MessagingException, IOException {
        List<String> files=new ArrayList<>();
        if(message.isMimeType("multipart/*"))
        {
            Multipart part = (Multipart) message.getContent();
            for(int i=0;i< part.getCount();i++)
            {
                BodyPart bodyPart = part.getBodyPart(i);

                if(Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()))
                {
                    InputStream inputStream = bodyPart.getInputStream();
                    File file=new File("src/main/resources/email/"+bodyPart.getFileName());
                    Files.copy(inputStream,file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    files.add(file.getAbsolutePath());
                }
            }
        }
        return files;

    }

    private String getContentFromEmailMessage(jakarta.mail.Message message) throws MessagingException, IOException {
        if(message.isMimeType("text/plain")|| message.isMimeType("text/html"))
        {
            String content = (String) message.getContent();
            return content;
        } else if (message.isMimeType("multipart/*")) {
            Multipart part = (Multipart) message.getContent();
            for(int i=0;i< part.getCount();i++)
            {
                BodyPart bodyPart = part.getBodyPart(i);
                if(bodyPart.isMimeType("text/plain"))
                {
                    return bodyPart.getContent().toString();
                }
            }

        }

        return "no content found";
    }
}
