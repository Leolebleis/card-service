package com.leolebleis.card.token.model;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Introspected
@Schema(name = "CardToken")
public class CardToken {

    @NotNull
    @Schema(description = "The card token.", example = "c74fffca-d59e-4a81-a205-f915c186b3a6")
    private UUID token;

}
