package com.leolebleis.card.payment.client.stripe;

import com.leolebleis.card.helpers.errors.payment.PaymentException;
import com.leolebleis.card.payment.model.stripe.error.StripeHttpResponseError;
import com.leolebleis.card.payment.model.stripe.response.PaymentIntentAuthorisationResponse;
import com.leolebleis.card.payment.model.stripe.request.StripePaymentIntentAuthorisationRequest;
import com.leolebleis.card.payment.helpers.StripeProperties;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class StripePaymentIntentClientFacade {

    private final StripePaymentIntentClient stripePaymentIntentClient;
    private final StripeProperties stripeProperties;

    @Inject
    public StripePaymentIntentClientFacade(StripePaymentIntentClient stripePaymentIntentClient,
                                           StripeProperties stripeProperties) {
        this.stripePaymentIntentClient = stripePaymentIntentClient;
        this.stripeProperties = stripeProperties;
    }

    public PaymentIntentAuthorisationResponse authorizePayment(final StripePaymentIntentAuthorisationRequest request) {
        log.info("Sending request to Stripe...");
        try {
            var response =
                    stripePaymentIntentClient.authorize(stripeProperties.getApiKey(), request.toFormUrlencoded());
            log.info("Response received with status[{}]", response.getStatus());
            return response.body();
        } catch (HttpClientResponseException e) {
            // Response from Stripe was >= 400.
            throw this.handleError(e);
        }
    }

    private PaymentException handleError(HttpClientResponseException e) {
        log.error("Error encountered when calling Stripe", e);

        if (e.getResponse().getBody(StripeHttpResponseError.class).isPresent()) {
            StripeHttpResponseError error = e.getResponse().getBody(StripeHttpResponseError.class).get();
            final String message = error.getError().getDeclineCode() == null
                    ? error.getError().getCode()
                    : error.getError().getDeclineCode();
            if (e.getStatus().equals(HttpStatus.PAYMENT_REQUIRED)) {
                return new PaymentException(String.format("Error authorising payment with Stripe[%s]",
                        message),
                        e,
                        HttpStatus.PAYMENT_REQUIRED);
            }

            return new PaymentException(String.format("Error authorising payment with Stripe[%s]",
                    message),
                    e,
                    HttpStatus.BAD_REQUEST);
        } else {
            return new PaymentException("Unexpected response from Stripe", e, e.getStatus());
        }
    }

}
