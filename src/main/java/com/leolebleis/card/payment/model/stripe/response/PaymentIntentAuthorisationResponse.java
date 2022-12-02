package com.leolebleis.card.payment.model.stripe.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentAuthorisationResponse {

    private String id;

    private String status;

}
