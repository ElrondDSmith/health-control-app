package org.snp.blood_pressure.mapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.snp.blood_pressure.dto.PressureRecordRqDto;
import org.snp.blood_pressure.dto.PressureRecordRsDto;
import org.snp.blood_pressure.entity.AppUser;
import org.snp.blood_pressure.entity.PressureRecord;
import org.snp.blood_pressure.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PressureRecordMapper {

    //private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private final UserRepository userRepository;

    public PressureRecordRsDto toRsDto(PressureRecord pressureRecord) {
        PressureRecordRsDto pressureRecordRsDto = new PressureRecordRsDto();
        //pressureRecordRsDto.setTimestamp(pressureRecord.getDateTime().format(DATE_TIME_FORMATTER));
        pressureRecordRsDto.setTimestamp(pressureRecord.getDateTime());
        pressureRecordRsDto.setSystolicPressure(pressureRecord.getSystolicPressure());
        pressureRecordRsDto.setDiastolicPressure(pressureRecord.getDiastolicPressure());
        pressureRecordRsDto.setPulse(pressureRecord.getPulse());
        return pressureRecordRsDto;
    }

    public List<PressureRecordRsDto> toRsDtoList(List<PressureRecord> pressureRecordList) {
        return pressureRecordList.stream().map(this::toRsDto).collect(Collectors.toList());
    }

    public PressureRecord toEntity(PressureRecordRqDto pressureRecordRqDto) {
        PressureRecord pressureRecord = new PressureRecord();
        Integer appUserId = userRepository.findUserByTelegramId(String.valueOf(pressureRecordRqDto.getTgId()))
                .map(AppUser::getId)
                .orElseThrow(() -> new IllegalArgumentException("пользователь не найден")); //String.format("Пользователь с tgId %s не найден", pressureRecordRqDto.getTgId())
        pressureRecord.setAppUserId(appUserId);
        pressureRecord.setSystolicPressure(pressureRecordRqDto.getSystolicPressure());
        pressureRecord.setDiastolicPressure(pressureRecordRqDto.getDiastolicPressure());
        pressureRecord.setPulse(pressureRecordRqDto.getPulse());
        pressureRecord.setDateTime(pressureRecordRqDto.getTimestamp());
        return pressureRecord;
    }
}
