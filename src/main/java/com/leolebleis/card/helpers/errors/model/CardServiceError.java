package com.leolebleis.card.helpers.errors.model;

import io.micronaut.http.hateoas.JsonError;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "A typical error thrown by the service.")
public class CardServiceError extends JsonError {

    @Schema(description = "Details about the error.",
            example = "Token [3ecbd14a-8249-46aa-8d43-473f0cbb58a1] does not exist")
    private String error;

    @Builder
    public CardServiceError(String error, String message) {
        super(message);
        this.error = error;
    }

}
