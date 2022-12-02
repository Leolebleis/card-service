package com.leolebleis.card.helpers;

import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;

import javax.inject.Inject;

public class BaseIntegrationTest {

    protected static final String X_API_KEY = "X-API-KEY";
    protected static final String X_PAYMENT_PROVIDER = "X-PAYMENT-PROVIDER";
    protected static final String BRAINTREE = "BRAINTREE";

    @Inject
    @Client("/")
    protected HttpClient client;

}
