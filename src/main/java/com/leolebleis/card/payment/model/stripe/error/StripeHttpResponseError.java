package com.leolebleis.card.payment.model.stripe.error;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripeHttpResponseError {

    private StripeError error;

}
