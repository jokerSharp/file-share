package io.project.controller;

import io.project.service.UserActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class ActivationController {

    private final UserActivationService userActivationService;

    @GetMapping("/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        boolean result = userActivationService.activation(id);
        if (result) {
            return ResponseEntity.ok().body("Activation is successful");
        }
        return ResponseEntity.internalServerError().body("Activation is failed");
    }
}
