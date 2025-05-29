package org.snp.blood_pressure.controller;

import lombok.RequiredArgsConstructor;
import org.snp.blood_pressure.dto.TelegramUserDtoRq;
import org.snp.blood_pressure.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/save_user")
    public ResponseEntity<String> saveUser(@Validated @RequestBody TelegramUserDtoRq telegramUserDtoRq) {
        userService.createUserIfNotExists(telegramUserDtoRq);
        return ResponseEntity.status(HttpStatus.CREATED).body("User saved successfully");
    }
}
