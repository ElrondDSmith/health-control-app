package org.snp.blood_pressure.service;

import lombok.RequiredArgsConstructor;
import org.snp.blood_pressure.dto.PressureRecordRqDto;
import org.snp.blood_pressure.dto.PressureRecordRsDto;
import org.snp.blood_pressure.entity.PressureRecord;
import org.snp.blood_pressure.mapper.PressureRecordMapper;
import org.snp.blood_pressure.repository.PressureRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PressureRecordService {

    private final PressureRecordRepository pressureRecordRepository;
    private final PressureRecordMapper pressureRecordMapper;
    private final TransactionTemplate transactionTemplate;

    public void createPressureRecord(PressureRecordRqDto pressureRecordRqDto) {
        PressureRecord pressureRecord = pressureRecordMapper.toEntity(pressureRecordRqDto);
        pressureRecordRepository.save(pressureRecord);
    }

    public List<PressureRecordRsDto> findByUserId(long id) {
        return pressureRecordMapper.toRsDtoList(pressureRecordRepository.findByUserId(id));
    }

    public List<PressureRecordRsDto> findByUserIdAndDate(long id, LocalDate date) {
        return pressureRecordMapper.toRsDtoList(pressureRecordRepository.findByUserIdAndDate(id, date));
    }

    public List<PressureRecordRsDto> findByUserIdAndLastNDays(long id, int days) {
        return pressureRecordMapper.toRsDtoList(pressureRecordRepository.findByUserIdAndLastNDays(id, days));
    }
}
