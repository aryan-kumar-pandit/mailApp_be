package com.send.mail.controller;


import com.send.mail.entity.EmailRequest;
import com.send.mail.helper.CustomResponse;
import com.send.mail.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/email")
@CrossOrigin("*")
public class EmailController {

    @Autowired
    private EmailService emailService;

    // send email without file
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request)
    {
        emailService.sendEmailWithHtml(request.getTo(),request.getSubject(),request.getMessage());
        CustomResponse res=new CustomResponse();
        res.setMessage("email sent successfully");
        res.setHttpStatus(HttpStatus.OK);
        res.setSuccess(true);
        return ResponseEntity.ok(res);

    }

    @PostMapping("/send-with-file")
    public ResponseEntity<?> sendWithFile(@RequestPart EmailRequest request,@RequestPart MultipartFile file) throws IOException {
        emailService.sendEmailWithFile(request.getTo(),request.getSubject(),request.getMessage(), file.getInputStream());

        CustomResponse res=new CustomResponse();
        res.setMessage("email sent successfully");
        res.setHttpStatus(HttpStatus.OK);
        res.setSuccess(true);
        return ResponseEntity.ok(res);
    }

}
