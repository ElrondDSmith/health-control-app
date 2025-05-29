package org.snp.blood_pressure.dto;

import lombok.Data;
import lombok.NonNull;


@Data
public class TelegramUserDtoRq {

    //@NonNull
    private String UserName;
    //@NonNull
    private String tgId;
}
