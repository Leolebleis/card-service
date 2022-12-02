package com.leolebleis.card.integration;

import com.leolebleis.card.helpers.BaseIntegrationTest;
import com.leolebleis.card.helpers.errors.model.CardServiceError;
import com.leolebleis.card.payment.client.braintree.BraintreeClientFacade;
import com.leolebleis.card.payment.client.stripe.StripePaymentIntentClient;
import com.leolebleis.card.payment.model.CardPaymentAuthorizationResponse;
import com.leolebleis.card.payment.model.PaymentStatus;
import com.leolebleis.card.payment.model.stripe.CardPaymentRequest;
import com.leolebleis.card.payment.model.stripe.error.PaymentIntent;
import com.leolebleis.card.payment.model.stripe.error.StripeError;
import com.leolebleis.card.payment.model.stripe.error.StripeHttpResponseError;
import com.leolebleis.card.payment.model.stripe.response.PaymentIntentAuthorisationResponse;
import com.leolebleis.card.token.model.CardDto;
import com.leolebleis.card.token.persistence.CardRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

import static io.micronaut.http.HttpRequest.POST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
class PaymentAuthorizationControllerTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/card/payment/authorize";
    private static final String TEST_API_KEY = "TEST_API_KEY";

    @Inject
    private CardRepository cardRepository;

    @MockBean(CardRepository.class)
    CardRepository cardRepository() {
        return mock(CardRepository.class);
    }

    @Inject
    private StripePaymentIntentClient stripePaymentIntentClient;

    @MockBean(StripePaymentIntentClient.class)
    StripePaymentIntentClient stripePaymentIntentClient() {
        return mock(StripePaymentIntentClient.class);
    }

    @Inject
    private BraintreeClientFacade braintreeClientFacade;

    @MockBean(BraintreeClientFacade.class)
    BraintreeClientFacade braintreeClientFacade() {
        return mock(BraintreeClientFacade.class);
    }

    @Test
    void testAuthorizeCardPayment_Stripe_Success() {
        final var token = UUID.randomUUID().toString();
        final var card = CardDto.builder()
                .number("41111111111111")
                .expiryYear("2025")
                .expiryMonth("02")
                .token(token)
                .id(1L)
                .cvc("020")
                .build();
        when(cardRepository.findByToken(token)).thenReturn(card);

        final var stripeResponse = PaymentIntentAuthorisationResponse.builder()
                .id("id")
                .status("success")
                .build();
        when(stripePaymentIntentClient.authorize(anyString(), anyMap()))
                .thenReturn(HttpResponse.ok(stripeResponse));

        final var cardPaymentRequest = CardPaymentRequest.builder()
                .amount("20.00")
                .currency("gbp")
                .firstName("Frodo")
                .lastName("Baggins")
                .token(token)
                .build();

        final HttpRequest<CardPaymentRequest> request = POST(BASE_URL, cardPaymentRequest)
                .header(X_API_KEY, TEST_API_KEY);
        final HttpResponse<PaymentIntentAuthorisationResponse> response =
                client.toBlocking().exchange(request, PaymentIntentAuthorisationResponse.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatus());
    }

    @Test
    void testAuthorizeCardPayment_Stripe_WrongAmountFormat() {
        final var token = UUID.randomUUID().toString();
        final var card = CardDto.builder()
                .number("41111111111111")
                .expiryYear("2025")
                .expiryMonth("02")
                .token(token)
                .id(1L)
                .cvc("020")
                .build();
        when(cardRepository.findByToken(token)).thenReturn(card);

        final var stripeResponse = PaymentIntentAuthorisationResponse.builder()
                .id("id")
                .status("success")
                .build();
        when(stripePaymentIntentClient.authorize(anyString(), anyMap()))
                .thenReturn(HttpResponse.ok(stripeResponse));

        final var cardPaymentRequest = CardPaymentRequest.builder()
                .amount("20.0a") // Should throw error
                .currency("gbp")
                .firstName("Gandalf")
                .lastName("The Grey")
                .token(token)
                .build();

        final HttpRequest<CardPaymentRequest> request = POST(BASE_URL, cardPaymentRequest)
                .header(X_API_KEY, TEST_API_KEY);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, PaymentIntentAuthorisationResponse.class);
        });

        assertNotNull(exception.getResponse().getBody());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getResponse().getStatus());
    }

    @Test
    void testAuthorizeCardPayment_Stripe_BadRequestError() {
        final var token = UUID.randomUUID().toString();
        final var card = CardDto.builder()
                .number("41111111111111")
                .expiryYear("2025")
                .expiryMonth("02")
                .token(token)
                .id(1L)
                .cvc("020")
                .build();
        when(cardRepository.findByToken(token)).thenReturn(card);

        final var stripeError = StripeError.builder()
                .code("400")
                .message("Unprocessable entity")
                .type("bad_request")
                .docUrl("docUrl")
                .build();
        final var stripeResponse = StripeHttpResponseError.builder()
                .error(stripeError)
                .build();

        final var stripeClientException = new HttpClientResponseException("TEST Could not process authorization",
                HttpResponse.badRequest(stripeResponse));
        when(stripePaymentIntentClient.authorize(anyString(), anyMap()))
                .thenThrow(stripeClientException);

        final var cardPaymentRequest = CardPaymentRequest.builder()
                .amount("20.00")
                .currency("gbp")
                .firstName("Gandalf")
                .lastName("The Grey")
                .token(token)
                .build();

        final HttpRequest<CardPaymentRequest> request = POST(BASE_URL, cardPaymentRequest)
                .header(X_API_KEY, TEST_API_KEY);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, PaymentIntentAuthorisationResponse.class);
        });

        assertNotNull(exception.getResponse().getBody());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getResponse().getStatus());
        assertEquals("Error authorising payment with Stripe[400]",
                exception.getResponse().getBody(CardServiceError.class).get().getMessage());

    }

    @Test
    void testAuthorizeCardPayment_Braintree_Success() {
        final var token = UUID.randomUUID().toString();
        final var card = CardDto.builder()
                .number("41111111111111")
                .expiryYear("2025")
                .expiryMonth("02")
                .token(token)
                .id(1L)
                .cvc("020")
                .build();
        when(cardRepository.findByToken(token)).thenReturn(card);

        when(braintreeClientFacade.authorizePayment(any(CardPaymentRequest.class), eq(card)))
                .thenReturn(CardPaymentAuthorizationResponse.builder()
                        .id("id")
                        .status(PaymentStatus.AUTHORIZED)
                        .build());
        final var cardPaymentRequest = CardPaymentRequest.builder()
                .amount("20.00")
                .currency("gbp")
                .firstName("Samwise")
                .lastName("Gamgee")
                .token(token)
                .build();

        final HttpRequest<CardPaymentRequest> request = POST(BASE_URL, cardPaymentRequest)
                .headers(Map.of(X_API_KEY, TEST_API_KEY,
                        X_PAYMENT_PROVIDER, BRAINTREE));
        final HttpResponse<PaymentIntentAuthorisationResponse> response =
                client.toBlocking().exchange(request, PaymentIntentAuthorisationResponse.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatus());
    }

    @Test
    void testAuthorizeCardPayment_Stripe_InsufficientFunds() {
        final var token = UUID.randomUUID().toString();
        final var card = CardDto.builder()
                .number("41111111111111")
                .expiryYear("2025")
                .expiryMonth("02")
                .token(token)
                .id(1L)
                .cvc("020")
                .build();
        when(cardRepository.findByToken(token)).thenReturn(card);

        final var paymentIntent = PaymentIntent.builder()
                .id("ID")
                .build();
        final var stripeError = StripeError.builder()
                .code("card_declined")
                .message("Your card has insufficient funds.")
                .type("bad_request")
                .declineCode("insufficient_funds")
                .docUrl("https://stripe.com/docs/error-codes/card-declined")
                .paymentIntent(paymentIntent)
                .build();
        final var stripeResponse = StripeHttpResponseError.builder()
                .error(stripeError)
                .build();

        final var clientException = new HttpClientResponseException("TEST Could not process authorization",
                HttpResponse.status(HttpStatus.PAYMENT_REQUIRED).body(stripeResponse));

        when(stripePaymentIntentClient.authorize(anyString(), anyMap()))
                .thenThrow(clientException);

        final var cardPaymentRequest = CardPaymentRequest.builder()
                .amount("20.00")
                .currency("gbp")
                .firstName("Frodo")
                .lastName("Baggins")
                .token(token)
                .build();

        final HttpRequest<CardPaymentRequest> request = POST(BASE_URL, cardPaymentRequest)
                .header(X_API_KEY, TEST_API_KEY);
        var exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, PaymentIntentAuthorisationResponse.class);
        });

        assertEquals(HttpStatus.PAYMENT_REQUIRED, exception.getStatus());
        assertTrue(exception.getResponse().getBody(CardServiceError.class).isPresent());

    }


}
