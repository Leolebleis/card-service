package com.leolebleis.card.payment.helpers;

import io.micronaut.context.annotation.Value;
import lombok.Data;

@Data
public class BraintreeProperties {

    @Value("${braintree.api.merchantId}")
    private String merchantId;

    @Value("${braintree.api.publicKey}")
    private String publicKey;

    @Value("${braintree.api.privateKey}")
    private String privateKey;
}
