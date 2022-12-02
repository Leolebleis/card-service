package com.leolebleis.card.payment.model.stripe.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripeError {

    private String code;

    @JsonProperty("decline_code")
    private String declineCode;

    @JsonProperty("doc_url")
    private String docUrl;

    private String message;

    private String type;

    private PaymentIntent paymentIntent;

}
