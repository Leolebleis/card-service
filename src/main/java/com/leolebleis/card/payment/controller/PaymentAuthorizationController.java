package com.leolebleis.card.payment.controller;

import com.leolebleis.card.payment.model.CardPaymentAuthorizationResponse;
import com.leolebleis.card.payment.model.PaymentProvider;
import com.leolebleis.card.payment.model.stripe.CardPaymentRequest;
import com.leolebleis.card.payment.service.CardPaymentService;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

@Controller("/card/payment")
@Slf4j
public class PaymentAuthorizationController {

    private final CardPaymentService cardPaymentService;

    @Value("${card-service.api.defaultProvider}")
    private static final String DEFAULT_PROVIDER = "STRIPE";

    public PaymentAuthorizationController(CardPaymentService cardPaymentService) {
        this.cardPaymentService = cardPaymentService;
    }

    @Post(value = "/authorize", produces = APPLICATION_JSON, consumes = APPLICATION_JSON)
    @Operation(summary = "Authorize a card transaction", tags = {"Transactions"},
            security = {@SecurityRequirement(name = "apiKey")}
    )
    @ApiResponse(responseCode = "201", description = "The authorized payment's status.")
    @ApiResponse(responseCode = "400", description = "Bad request. All parameters are required and must not be empty.")
    @ApiResponse(responseCode = "401", description = "Authentication information is missing or invalid.")
    @ApiResponse(responseCode = "402", description = "There was an error authorizing the payment with the payment provider.")
    @ApiResponse(responseCode = "404", description = "No path seems to match this request.")
    @ApiResponse(responseCode = "5XX", description = "Unexpected server error.")
    public HttpResponse<CardPaymentAuthorizationResponse> authorize(
            @Body @Valid CardPaymentRequest request,
            @Header(value = "X-PAYMENT-PROVIDER", defaultValue = DEFAULT_PROVIDER) PaymentProvider provider) {
        log.info("Authorizing card payment[{}]", request);
        return HttpResponse.created(cardPaymentService.authorizePayment(request, provider));
    }

}
