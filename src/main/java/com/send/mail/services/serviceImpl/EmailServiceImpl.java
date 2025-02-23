package com.send.mail.services.serviceImpl;

import com.send.mail.services.EmailService;
import jakarta.mail.MessagingException;
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
}
