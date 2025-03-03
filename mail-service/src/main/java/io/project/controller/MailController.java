package io.project.controller;

import io.project.dto.MailParams;
import io.project.service.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/mail")
@RestController
public class MailController {

    private final MailSender mailSender;

    @PostMapping("/send")
    public ResponseEntity<?> sendActivationMail(@RequestBody MailParams mailParams) {
        mailSender.send(mailParams);
        return ResponseEntity.ok().build();
    }
}
