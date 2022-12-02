package com.leolebleis.card.payment.model;

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
public class CardPaymentAuthorizationResponse {

    @NotNull
    @Schema(description = "The transaction ID from the corresponding payment provider.", example = "x7e2weur1")
    private String id;

    @NotNull
    @Schema(description = "The payment status.", example = "AUTHORIZED")
    private PaymentStatus status;

}
