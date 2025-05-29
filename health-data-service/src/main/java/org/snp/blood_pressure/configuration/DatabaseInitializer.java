package org.snp.blood_pressure.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer {
    private final JdbcTemplate jdbcTemplate;

    @Value("${custom.database.schema}")
    private String schema;

    @Value("${custom.database.tables.users}")
    private String usersTable;

    @Value("${custom.database.tables.pressure_records}")
    private String pressureRecordsTable;

    @PostConstruct
    public void initializingDataBase() {
        schemaAndTablesInit();
    }

    private void schemaAndTablesInit() {
        String sql = String.format("""
                CREATE SCHEMA IF NOT EXISTS %s;
                Create table if not exists %s.%s (
                	id serial primary key,
                	name varchar(100),
                	email varchar(100),
                	tg_id varchar(100));
                Create table if not exists %s.%s (
                	id serial primary key,
                	record_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                	user_id integer references %s.%s(id),
                	systolic_pressure INTEGER,
                    diastolic_pressure INTEGER,
                    pulse INTEGER);
                """, schema, schema, usersTable, schema, pressureRecordsTable, schema, usersTable);
        jdbcTemplate.execute(sql);
    }
}
