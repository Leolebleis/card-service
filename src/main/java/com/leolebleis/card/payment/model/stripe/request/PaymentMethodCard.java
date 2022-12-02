package com.leolebleis.card.payment.model.stripe.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodCard {

    private String number;

    private String cvc;

    private String expiryMonth;

    private String expiryYear;

}
