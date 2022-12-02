package com.leolebleis.card.integration;

import com.leolebleis.card.helpers.BaseIntegrationTest;
import com.leolebleis.card.helpers.errors.model.CardServiceError;
import com.leolebleis.card.token.model.Card;
import com.leolebleis.card.token.model.CardDto;
import com.leolebleis.card.token.model.CardToken;
import com.leolebleis.card.token.persistence.CardRepository;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

import static io.micronaut.http.HttpRequest.POST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
class TokenControllerTest extends BaseIntegrationTest {

    private static final String TEST_API_KEY = "TEST_API_KEY";
    private static final String BASE_URL = "/card/token";

    @Inject
    private CardRepository cardRepository;

    @MockBean(CardRepository.class)
    CardRepository cardRepository() {
        return mock(CardRepository.class);
    }

    @Test
    void testCreateToken_Success() {
        final var cvc = "123";
        final var expiryMonth = "01";
        final var expiryYear = "1992";
        final var number = "123123";
        final var card = Card.builder()
                .cvc(cvc)
                .expiryMonth(expiryMonth)
                .expiryYear(expiryYear)
                .number(number)
                .build();
        final var expectedToken = UUID.randomUUID().toString();

        when(cardRepository.save(eq(number), eq(expiryMonth), eq(expiryYear), eq(cvc), any(String.class)))
                .thenReturn(CardDto.builder()
                        .id(1L).cvc(cvc).expiryMonth(expiryMonth).expiryYear(expiryYear).number(number)
                        .token(expectedToken)
                        .build());

        final HttpRequest<Card> request = POST(BASE_URL, card)
                .header(X_API_KEY, TEST_API_KEY);
        final HttpResponse<CardToken> response = client.toBlocking().exchange(request, CardToken.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getBody());

        assertEquals(UUID.fromString(expectedToken), response.getBody().get().getToken());
    }

    @Test
    void testCreateToken_MissingField() {
        final var cvc = "123";
        final var expiryMonth = "01";
        final var number = "123123";
        final var card = Card.builder()
                .cvc(cvc)
                .expiryMonth(expiryMonth)
                .number(number)
                .build();

        final HttpRequest<Card> request = POST(BASE_URL, card)
                .header(X_API_KEY, TEST_API_KEY);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, CardToken.class);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertNotNull(exception.getResponse().getBody());

        assertEquals("card.expiryYear: must not be null",
                exception.getResponse().getBody(CardServiceError.class).get().getMessage());
    }

    @Test
    void testCreateToken_MalformedRequest() {
        final var cvc = "123";
        final var expiryMonth = "011"; // Should cause an error
        final var expiryYear = "030";
        final var number = "123123";
        final var card = Card.builder()
                .cvc(cvc)
                .expiryMonth(expiryMonth)
                .expiryYear(expiryYear)
                .number(number)
                .build();

        final var dataAccessException = new DataAccessException("TEST expiryYear is VARCHAR(2)");

        when(cardRepository.save(eq(number), eq(expiryMonth), eq(expiryYear), eq(cvc), any(String.class)))
                .thenThrow(dataAccessException);

        final HttpRequest<Card> request = POST(BASE_URL, card)
                .header(X_API_KEY, TEST_API_KEY);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, CardToken.class);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertNotNull(exception.getResponse().getBody(CardServiceError.class));
        assertEquals("Malformed request",
                exception.getResponse().getBody(CardServiceError.class).get().getMessage());
    }



}
