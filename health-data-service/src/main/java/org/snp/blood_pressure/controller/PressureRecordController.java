package org.snp.blood_pressure.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snp.blood_pressure.dto.PressureRecordRqDto;
import org.snp.blood_pressure.dto.PressureRecordRsDto;
import org.snp.blood_pressure.service.PressureRecordService;
import org.snp.blood_pressure.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PressureRecordController {

    private final PressureRecordService pressureRecordService;
    private final UserService userService;


    @GetMapping("/id")
    public String findById() {
        return "oki";
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@Validated @RequestBody PressureRecordRqDto pressureRecordRqDto) {
        pressureRecordService.createPressureRecord(pressureRecordRqDto);
        return ResponseEntity.status(CREATED).body("Pressure record saved successfully");
    }

    @GetMapping("/user/{tgId}")
    public ResponseEntity<List<PressureRecordRsDto>>  getAllRecordsByUserId(
            @PathVariable long tgId) {
        List<PressureRecordRsDto> records = pressureRecordService.findByUserId(tgIdToAppUserId(tgId));
        if (records.isEmpty()) {
            return ResponseEntity.status(NOT_FOUND).body(null);
        }
        return ResponseEntity.status(OK).body(records);
    }

    @GetMapping("/user/{tgId}/date")
    public ResponseEntity<List<PressureRecordRsDto>>  getRecordsByUserIdAndDate(
            @PathVariable long tgId,
            @RequestParam LocalDate date
    ) {
        List<PressureRecordRsDto> records = pressureRecordService.findByUserIdAndDate(tgIdToAppUserId(tgId), date);
        if (records.isEmpty()) {
            return ResponseEntity.status(NOT_FOUND).body(null);
        }
        return ResponseEntity.status(OK).body(records);
    }

    @GetMapping("/user/{tgId}/number_of_days")
    public ResponseEntity<List<PressureRecordRsDto>>  getRecordsByUserIdAndLastNDays(
            @PathVariable long tgId,
            @RequestParam int numberOfDays
    ) {
        List<PressureRecordRsDto> records = pressureRecordService.findByUserIdAndLastNDays(tgIdToAppUserId(tgId), numberOfDays);
        if (records.isEmpty()) {
            log.info("Данные не найдены");
            return ResponseEntity.status(NOT_FOUND).body(null);
        }
        return ResponseEntity.status(OK).body(records);
    }

    private Integer tgIdToAppUserId(long tgId) {
        return userService.findAppUserIdByTgId(String.valueOf(tgId))
                .orElseThrow(() -> new RuntimeException(String.format("Пользователь %s, не найден", tgId)));
    }
}
