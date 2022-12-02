package com.leolebleis.card.token.persistence;

import com.leolebleis.card.token.model.CardDto;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * The repository for Card data. This contains the card data and the associated cardToken.
 */
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CardRepository extends PageableRepository<CardDto, Long> {

    CardDto save(@NonNull @NotBlank String number,
                 @NonNull @NotBlank String expiryMonth,
                 @NonNull @NotBlank String expiryYear,
                 @NonNull @NotBlank String cvc,
                 @NonNull @NotBlank String token);

    CardDto findByToken(String token);

    void deleteByToken(@NotNull String token);

}
