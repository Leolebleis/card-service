package com.leolebleis.card.token.model;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MappedEntity("card")
public class CardDto {

    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    private Long id;

    @NotNull
    private String number;

    @NotNull
    private String expiryMonth;

    @NotNull
    private String expiryYear;

    @NotNull
    private String cvc;

    @NotNull
    private String token;

}
