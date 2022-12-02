package com.leolebleis.card.payment.model.stripe;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Introspected
@Schema(description = "The customer's data and card token.")
public class CardPaymentRequest {

    @NotNull
    @Pattern(regexp = "^\\d{1,6}(\\.\\d{1,2})?$")
    @Schema(example = "14.64", description = "The transaction's amount, with at most 2 decimal places.")
    private String amount;

    @NotNull
    @Schema(example = "gbp", description = "The currency the transaction is in.")
    private String currency;

    @NotNull
    @Schema(example = "Bilbo", description = "The first name of the customer making the payment.")
    private String firstName;

    @NotNull
    @Schema(example = "Baggins", description = "The last name of the customer making the payment.")
    private String lastName;

    @NotNull
    @Schema(example = "The card token for the card to be used for that transaction.",
            description = "65a5e2c1-e7d6-4f90-a853-041396d33f61")
    private String token;

}
