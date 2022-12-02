package com.leolebleis.card.payment.client.stripe;

import com.leolebleis.card.payment.model.stripe.error.StripeHttpResponseError;
import com.leolebleis.card.payment.model.stripe.response.PaymentIntentAuthorisationResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

import java.util.Map;

import static io.micronaut.http.MediaType.APPLICATION_FORM_URLENCODED;
import static io.micronaut.http.MediaType.APPLICATION_JSON;

@Client(value = "${stripe.api.url}/${stripe.api.version}/payment_intents", errorType = StripeHttpResponseError.class)
public interface StripePaymentIntentClient {

    @Post(consumes = APPLICATION_JSON, produces = APPLICATION_FORM_URLENCODED)
    HttpResponse<PaymentIntentAuthorisationResponse> authorize(@Header(value = "Authorization") String apiKey,
                                                               @Body Map<String, Object> body);

}
