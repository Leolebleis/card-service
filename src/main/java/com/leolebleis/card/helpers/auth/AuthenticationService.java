package com.leolebleis.card.helpers.auth;

import com.leolebleis.card.helpers.config.CardServiceProperties;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

@Slf4j
@Filter("/**")
public class AuthenticationService extends OncePerRequestHttpServerFilter {
    private static final String X_API_KEY = "X-API-KEY";

    private static final List<String> STRICT_IGNORED_PATHS = List.of(
            "/swagger/card-service-0.1.yml");

    private final CardServiceProperties cardServiceProperties;

    @Inject
    public AuthenticationService(CardServiceProperties cardServiceProperties) {
        this.cardServiceProperties = cardServiceProperties;
    }

    /**
     * Util method to verify API keys.
     *
     * @param apiKey The API key from a request.
     * @return true if the API key is valid, otherwise false.
     */
    private boolean isValid(final String apiKey) {
        return apiKey.equals(cardServiceProperties.getApiKey());
    }

    /**
     * Filter method that will be executed once per request reaching the API. It will determine whether the
     * X-API-KEY header is present, and if so, validate it against the API key.
     * If unsuccessful, the response will be 401 Unauthorized.
     *
     * @param request The HTTP request.
     * @param chain   The server filter chain.
     * @return 401 Unauthorized if authentication is unsuccessful, otherwise the chain will proceed.
     */
    @Override
    protected Publisher<MutableHttpResponse<?>> doFilterOnce(HttpRequest<?> request, ServerFilterChain chain) {
        log.info("Authenticating request");

        if (isRequestIgnored(request.getPath())) {
            return chain.proceed(request);
        } else if (request.getHeaders().get(X_API_KEY) == null) {
            return Flowable.just(HttpResponse.unauthorized());
        } else if (!isValid(Objects.requireNonNull(request.getHeaders().get(X_API_KEY)))) {
            return Flowable.just(HttpResponse.unauthorized());
        } else {
            return chain.proceed(request);
        }
    }

    /**
     * Check to see if the path is contained in the strictly ignored paths.
     * This is to allow swagger specs generation.
     *
     * @param path The path to check against.
     * @return true if the path is ignored, otherwise false.
     */
    public boolean isRequestIgnored(final String path) {
        var isIgnored = false;
        for (String ignoredPath : STRICT_IGNORED_PATHS) {
            if (path.equals(ignoredPath)) {
                isIgnored = true;
                break;
            }
        }

        return isIgnored;
    }
}
