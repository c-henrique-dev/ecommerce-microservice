package br.com.loomi.customermicroservice.controllers;

import br.com.loomi.customermicroservice.services.MailService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customer/mail")
public class MailController {
    private MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/confirm")
    @Hidden
    public ResponseEntity<String> confirmEmail(@RequestParam String email) {
        return ResponseEntity.ok(mailService.confirmEmail(email));
    }
}
