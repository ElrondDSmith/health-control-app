package org.snp.telegraminputservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pressure.limits")
@Data
public class PressureLimits {

    private int minSys;
    private int maxSys;
    private int minDia;
    private int maxDia;
    private int minPulse;
    private int maxPulse;
}
