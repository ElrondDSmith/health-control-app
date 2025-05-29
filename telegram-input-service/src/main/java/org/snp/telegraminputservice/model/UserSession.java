package org.snp.telegraminputservice.model;

import lombok.Data;

@Data
public class UserSession {
    private UserState userState = UserState.NONE;
    private Long userId;
    private String userName;
    private Integer systolic;
    private Integer diastolic;
    private Integer pulse;
}
