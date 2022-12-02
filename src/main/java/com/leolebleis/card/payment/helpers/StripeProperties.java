package com.leolebleis.card.payment.helpers;

import io.micronaut.context.annotation.Value;
import lombok.Data;

@Data
public class StripeProperties {

    @Value("${stripe.api.secret}")
    private String apiKey;
}
