package org.snp.telegraminputservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "sleep")
@Data
public class SleepProperties {

    private int timeoutMinutes;
    private List<String> pingUrls;
}
