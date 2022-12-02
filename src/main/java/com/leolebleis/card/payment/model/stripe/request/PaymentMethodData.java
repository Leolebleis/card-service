package com.leolebleis.card.payment.model.stripe.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodData {

    private String type;

    private PaymentMethodCard card;

    private BillingDetails billingDetails;

}
