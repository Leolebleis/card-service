package com.leolebleis.card.token.controller;

import com.leolebleis.card.token.model.Card;
import com.leolebleis.card.token.model.CardToken;
import com.leolebleis.card.token.service.TokenService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.validation.Valid;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

@Controller("/card/token")
@Slf4j
public class TokenController {

    private final TokenService tokenService;

    @Inject
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Post(consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @Operation(summary = "Create a card token", tags = {"Tokens"},
            security = {@SecurityRequirement(name = "apiKey")}
    )
    @ApiResponse(responseCode = "201", description = "The card token was successfully created.")
    @ApiResponse(responseCode = "400", description = "Bad request. All parameters are required and must not be empty.")
    @ApiResponse(responseCode = "401", description = "Authentication information is missing or invalid.")
    @ApiResponse(responseCode = "404", description = "No path seems to match this request.")
    @ApiResponse(responseCode = "5XX", description = "Unexpected server error.")
    public HttpResponse<CardToken> tokenize(@Body @Valid Card card) {
        log.info("Tokenize request received [{}]", card);

        return HttpResponse.created(tokenService.tokenize(card));
    }

}
