package com.leolebleis.card.helpers.errors.token;

import com.leolebleis.card.helpers.errors.model.CardServiceError;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.hateoas.Link;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {TokenException.class, ExceptionHandler.class})
public class TokenExceptionHandler implements ExceptionHandler<TokenException, HttpResponse<CardServiceError>> {

    private static final String TOKEN_ERROR_MESSAGE = "token_error";

    /**
     * This will handle all the TokenException errors thrown in the service and return the correct HttpResponse.
     *
     * @param request The originating request.
     * @param e       The TokenException error raised.
     * @return The appropriate HttpResponse.
     */
    @Override
    public HttpResponse handle(HttpRequest request, TokenException e) {
        var body = CardServiceError.builder()
                .message(e.getMessage())
                .error(TOKEN_ERROR_MESSAGE)
                .build()
                .link(Link.SELF, Link.of(request.getUri()));
        if (e.getStatus() != null) {
            HttpResponse.status(e.getStatus()).body(body);
        }
        return HttpResponse.badRequest(body);
    }

}
