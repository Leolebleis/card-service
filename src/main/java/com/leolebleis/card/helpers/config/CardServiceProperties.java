package com.leolebleis.card.helpers.config;

import io.micronaut.context.annotation.Value;
import lombok.Data;

@Data
public class CardServiceProperties {

    @Value("${card-service.api.key}")
    private String apiKey;
}
