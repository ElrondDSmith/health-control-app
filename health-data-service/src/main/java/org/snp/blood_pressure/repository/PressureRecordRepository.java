package org.snp.blood_pressure.repository;

import lombok.RequiredArgsConstructor;
import org.snp.blood_pressure.entity.PressureRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class PressureRecordRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(PressureRecord pressureRecord) {
        String sql = """
                insert into health.pressure_records (user_id, record_time, systolic_pressure, diastolic_pressure, pulse)
                    values (?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                pressureRecord.getAppUserId(),
                pressureRecord.getDateTime(),
                pressureRecord.getSystolicPressure(),
                pressureRecord.getDiastolicPressure(),
                pressureRecord.getPulse());
    }

    public List<PressureRecord> findByUserId(long userId) {
        String sql = """
                select * from health.pressure_records where user_id = ?
                """;
        try (Stream<PressureRecord> stream = jdbcTemplate.queryForStream(sql, this::pressureRecordRowMapper, userId)){
            return stream.toList();
        }
    }

    public List<PressureRecord> findByUserIdAndDate(long userId, LocalDate date) {
        String sql = """
                select * from health.pressure_records where user_id = ?
                 and record_time >= ? and record_time < ?
                """;
        try (Stream<PressureRecord> stream = jdbcTemplate.queryForStream(sql,
                this::pressureRecordRowMapper,
                userId,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay())){
            return stream.toList();
        }
    }

    public List<PressureRecord> findByUserIdAndLastNDays(long userId, int days) {

        String sql = """
                select * from health.pressure_records where user_id = ?
                 and record_time >= current_date - ((? - 1) * interval '1 day')
                 and record_time < (current_date + interval '1 day')
                 order by record_time desc
                """;
        try (Stream<PressureRecord> stream = jdbcTemplate.queryForStream(sql, this::pressureRecordRowMapper, userId, days)){
            return stream.toList();
        }
    }

    private PressureRecord pressureRecordRowMapper(ResultSet rs, int rowNumber) {
        try {
            PressureRecord pressureRecord = new PressureRecord();
            pressureRecord.setId(rs.getInt("id"));

            Timestamp timestamp = rs.getTimestamp("record_time");
            pressureRecord.setDateTime(timestamp != null ? timestamp.toLocalDateTime() : null);

            pressureRecord.setAppUserId(rs.getInt("user_id"));
            pressureRecord.setSystolicPressure(rs.getInt("systolic_pressure"));
            pressureRecord.setDiastolicPressure(rs.getInt("diastolic_pressure"));
            pressureRecord.setPulse(rs.getInt("pulse"));
            return pressureRecord;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}