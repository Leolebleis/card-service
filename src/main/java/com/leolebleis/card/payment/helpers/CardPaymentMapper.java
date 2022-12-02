package com.leolebleis.card.payment.helpers;

import com.braintreegateway.Transaction;
import com.leolebleis.card.helpers.errors.payment.PaymentException;
import com.leolebleis.card.payment.model.CardPaymentAuthorizationResponse;
import com.leolebleis.card.payment.model.PaymentStatus;
import com.leolebleis.card.payment.model.stripe.CardPaymentRequest;
import com.leolebleis.card.payment.model.stripe.request.BillingDetails;
import com.leolebleis.card.payment.model.stripe.request.PaymentMethodCard;
import com.leolebleis.card.payment.model.stripe.request.PaymentMethodData;
import com.leolebleis.card.payment.model.stripe.request.StripePaymentIntentAuthorisationRequest;
import com.leolebleis.card.payment.model.stripe.response.PaymentIntentAuthorisationResponse;
import com.leolebleis.card.token.model.CardDto;
import io.micronaut.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CardPaymentMapper {

    private CardPaymentMapper() {
        // Private constructor.
    }

    private static final String CARD = "card";

    public static StripePaymentIntentAuthorisationRequest toStripePaymentRequest(CardPaymentRequest cardPaymentRequest,
                                                                                 CardDto cardDto) {
        var card = PaymentMethodCard.builder()
                .cvc(cardDto.getCvc())
                .number(cardDto.getNumber())
                .expiryMonth(cardDto.getExpiryMonth())
                .expiryYear(cardDto.getExpiryYear())
                .build();
        var data = PaymentMethodData.builder()
                .billingDetails(BillingDetails.builder()
                        .name(cardPaymentRequest.getFirstName() + cardPaymentRequest.getLastName())
                        .build())
                .card(card)
                .type(CARD)
                .build();
        var amount = toStripeAmount(cardPaymentRequest.getAmount());

        return StripePaymentIntentAuthorisationRequest.builder()
                .amount(amount)
                .currency(cardPaymentRequest.getCurrency())
                .paymentMethodData(data)
                .build();
    }

    /**
     * Method used to format the amount according to Stripe's standards.
     * The Stripe API does not use decimal places for its amount.
     *
     * @param amount The original amount.
     * @return The formatted amount according to Stripe's specs.
     */
    private static String toStripeAmount(String amount) {
        var multipliedAmount = new BigDecimal(amount).multiply(new BigDecimal("100"));

        return multipliedAmount.setScale(0, RoundingMode.UNNECESSARY).toString();
    }

    public static CardPaymentAuthorizationResponse toCardPaymentAuthorisationResponse(Transaction transaction) {
        if (transaction.getStatus() == Transaction.Status.AUTHORIZED) {
            return CardPaymentAuthorizationResponse.builder()
                    .id(transaction.getId())
                    .status(PaymentStatus.AUTHORIZED)
                    .build();
        } else {
            throw new PaymentException(String.format("Error authorizing payment with Braintree[%s]",
                    transaction.getStatus()), HttpStatus.PAYMENT_REQUIRED);
        }
    }

    public static CardPaymentAuthorizationResponse toCardPaymentAuthorisationResponse(
            PaymentIntentAuthorisationResponse stripeResponse) {
        return CardPaymentAuthorizationResponse.builder()
                .id(stripeResponse.getId())
                .status(PaymentStatus.AUTHORIZED)
                .build();
    }

}
