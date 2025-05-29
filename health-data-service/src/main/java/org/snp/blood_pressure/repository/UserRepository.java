package org.snp.blood_pressure.repository;

import lombok.RequiredArgsConstructor;
import org.snp.blood_pressure.entity.AppUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public void createUser(AppUser appUser) {
        String sql = """
                insert into health.users (name, email, tg_id) values (?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                appUser.getName(),
                appUser.getEmail(),
                appUser.getTgId());
    }

    public List<AppUser> findAllAppUsers() {
        String sql = """
                Select * from health.users
                """;
        return jdbcTemplate.queryForStream(sql, this::appUserRowMapper).toList();
    }

    public Optional<AppUser> findUserByTelegramId(String tgId) {
        String sql = "Select * from health.users where tg_id = ?";
        return jdbcTemplate.query(sql, this::appUserRowMapper, tgId )
                .stream().
                findFirst();
    }

    private AppUser appUserRowMapper(ResultSet rs, int rowNumber) {
        try {
            AppUser appUser = new AppUser();
            appUser.setId(rs.getInt("id"));
            appUser.setName(rs.getString("name"));
            appUser.setEmail(rs.getString("email"));
            appUser.setTgId(rs.getString("tg_id"));
            return appUser;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
