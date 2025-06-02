package org.snp.telegraminputservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Value
public class UserRegDto {

    private Long tgId;
    private String userName;
}
