package com.leolebleis.card.token.model;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Introspected
public class Card {

    @NotNull
    @Schema(description = "A card number.", example = "4111111111111111")
    private String number;

    @NotNull
    @Schema(description = "The expiration month of the card.", example = "02")
    private String expiryMonth;

    @NotNull
    @Schema(description = "The expiration year of the card.", example = "2025")
    private String expiryYear;

    @NotNull
    @Schema(description = "The CVC of the card.", example = "020")
    private String cvc;

}
