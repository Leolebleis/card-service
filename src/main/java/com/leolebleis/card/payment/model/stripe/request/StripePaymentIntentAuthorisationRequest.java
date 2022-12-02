package com.leolebleis.card.payment.model.stripe.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripePaymentIntentAuthorisationRequest {

    private String amount;

    private String currency;

    private PaymentMethodData paymentMethodData;

    /**
     * Micronaut does not have a graceful way of serializing x-www-form-urlencoded data that has custom
     *
     * @return A correctly formatted object ready for serialization in x-www-form-urlencoded.
     */
    public Map<String, Object> toFormUrlencoded() {
        HashMap<String, Object> form = new HashMap<>();
        form.put("amount", this.amount);
        form.put("currency", this.currency);
        form.put("payment_method_data[type]", this.paymentMethodData.getType());
        form.put("payment_method_data[card][exp_year]", this.paymentMethodData.getCard().getExpiryYear());
        form.put("payment_method_data[card][exp_month]", this.paymentMethodData.getCard().getExpiryMonth());
        form.put("payment_method_data[card][number]", this.paymentMethodData.getCard().getNumber());
        form.put("payment_method_data[card][cvc]", this.paymentMethodData.getCard().getCvc());
        form.put("payment_method_data[billing_details][name]", this.paymentMethodData.getBillingDetails().getName());
        form.put("confirm", "true");
        form.put("capture_method", "manual");

        return form;
    }

}
