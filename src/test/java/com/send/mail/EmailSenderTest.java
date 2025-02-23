package com.send.mail;

import com.send.mail.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
public class EmailSenderTest {

    @Autowired
    private EmailService emailService;

    @Test
    void emailSendTest(){
        System.out.println("sent email");
        emailService.sendEmail("kumarryan68@gmail.com","testing mail trap","message testing successful");

    }

    @Test
    void emailSendMailTrapTest(){
        System.out.println("sent email");
        emailService.sendEmail("aryankumarpandito@gmail.com","testing mail trap","message testing successful");

    }

    @Test
    void sendEmailWithHtmlTest()
    {
        String html=""+"<h1 style='color:red;border:1px solid red;'>welcome to html mail testing</h1>"+"";

        emailService.sendEmailWithHtml("aryankumarpandito@gmail.com","testing mail trap",html);
    }

    @Test
    void sendEmailWithFileTest()
    {
        File f=new File("/Users/apple/IdeaProjects/mailApp_be/src/main/resources/static/about.html");
        emailService.sendEmailWithFile("kumarryan68@gmail.com","email sent with attachment","Hi aryan ",f);
    }


}
