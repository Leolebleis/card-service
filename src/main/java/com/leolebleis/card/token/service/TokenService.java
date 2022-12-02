package com.leolebleis.card.token.service;

import com.leolebleis.card.helpers.errors.token.TokenException;
import com.leolebleis.card.token.model.Card;
import com.leolebleis.card.token.model.CardToken;
import com.leolebleis.card.token.persistence.CardRepository;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
@Slf4j
public class TokenService {

    private final CardRepository cardRepository;

    @Inject
    public TokenService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Service method to tokenize a card.
     *
     * @param card The card to tokenize.
     * @return a random UUID token.
     */
    public CardToken tokenize(final Card card) {
        log.info("Tokenizing and saving card...");
        final var token = UUID.randomUUID();
        try {
            final var cardDto = cardRepository.save(card.getNumber(), card.getExpiryMonth(),
                    card.getExpiryYear(), card.getCvc(), token.toString());
            log.info("Card tokenized[{}]", cardDto);
            return CardToken.builder().token(UUID.fromString(cardDto.getToken())).build();
        } catch (DataAccessException e) {
            // This will be thrown if the data does not fit the SQL definition detailed in the flyway script.
            // For example, if month is 3 characters long, the database will reject that.
            throw new TokenException("Malformed request", e, HttpStatus.BAD_REQUEST);
        }
    }

}
