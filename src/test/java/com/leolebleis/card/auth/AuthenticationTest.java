package com.leolebleis.card.auth;

import com.leolebleis.card.helpers.BaseIntegrationTest;
import com.leolebleis.card.helpers.auth.AuthenticationService;
import com.leolebleis.card.payment.model.stripe.CardPaymentRequest;
import com.leolebleis.card.payment.model.stripe.response.PaymentIntentAuthorisationResponse;
import com.leolebleis.card.token.model.Card;
import com.leolebleis.card.token.model.CardToken;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

import static io.micronaut.http.HttpRequest.POST;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class AuthenticationTest extends BaseIntegrationTest {

    private static final String TOKEN_BASE_URL = "/card/token";
    private static final String PAYMENT_BASE_URL = "/card/payment/authorize";

    @Inject
    private AuthenticationService authenticationService;

    @Test
    void testCreateToken_WrongApiKey() {
        final var card = Card.builder().build();
        final HttpRequest<Card> request = POST(TOKEN_BASE_URL, card)
                .header(X_API_KEY, "Wrong API key");

        var exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, CardToken.class);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void testAuthorizePayment_WrongApiKey() {
        final var token = UUID.randomUUID().toString();

        final var cardPaymentRequest = CardPaymentRequest.builder()
                .amount("20.00")
                .currency("gbp")
                .firstName("Frodo")
                .lastName("Baggins")
                .token(token)
                .build();

        final HttpRequest<CardPaymentRequest> request = POST(PAYMENT_BASE_URL, cardPaymentRequest)
                .header(X_API_KEY, "Wrong API key");
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, PaymentIntentAuthorisationResponse.class);

        });

        assertNotNull(exception.getResponse().getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void testCreateToken_MissingApiKey() {
        final var card = Card.builder().build();
        final HttpRequest<Card> request = POST(TOKEN_BASE_URL, card);
        var exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, CardToken.class);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void testIgnoredPaths() {
        assertTrue(authenticationService.isRequestIgnored("/swagger/ard-service-0.1.yml"));
        assertFalse(authenticationService.isRequestIgnored("/card/token"));
        assertFalse(authenticationService.isRequestIgnored("/card/payment/authorize"));
        assertFalse(authenticationService.isRequestIgnored("/swagger/test"));
    }

}
