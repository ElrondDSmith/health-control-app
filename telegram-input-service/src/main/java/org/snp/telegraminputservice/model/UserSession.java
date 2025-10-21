package org.snp.telegraminputservice.model;

import lombok.Data;

import java.time.Instant;

@Data
public class UserSession {

    private Long chatId;
    private UserState userState = UserState.NONE;
    private Long userId;
    private String userName;
    private Integer systolic;
    private Integer diastolic;
    private Integer pulse;
    private Instant lastActivity;
}
