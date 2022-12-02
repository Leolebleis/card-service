package com.leolebleis.card.payment.service;

import com.leolebleis.card.helpers.errors.payment.PaymentException;
import com.leolebleis.card.helpers.errors.token.TokenException;
import com.leolebleis.card.payment.client.braintree.BraintreeClientFacade;
import com.leolebleis.card.payment.client.stripe.StripePaymentIntentClientFacade;
import com.leolebleis.card.payment.helpers.CardPaymentMapper;
import com.leolebleis.card.payment.model.CardPaymentAuthorizationResponse;
import com.leolebleis.card.payment.model.PaymentProvider;
import com.leolebleis.card.payment.model.stripe.CardPaymentRequest;
import com.leolebleis.card.payment.model.stripe.response.PaymentIntentAuthorisationResponse;
import com.leolebleis.card.token.persistence.CardRepository;
import io.micronaut.data.exceptions.EmptyResultException;
import io.micronaut.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class CardPaymentService {

    private final BraintreeClientFacade braintreeClientFacade;
    private final StripePaymentIntentClientFacade stripePaymentIntentClientFacade;
    private final CardRepository cardRepository;

    @Inject
    public CardPaymentService(StripePaymentIntentClientFacade stripePaymentIntentClientFacade,
                              CardRepository cardRepository,
                              BraintreeClientFacade braintreeClientFacade) {
        this.stripePaymentIntentClientFacade = stripePaymentIntentClientFacade;
        this.cardRepository = cardRepository;
        this.braintreeClientFacade = braintreeClientFacade;
    }

    public CardPaymentAuthorizationResponse authorizePayment(CardPaymentRequest cardPaymentRequest,
                                                             PaymentProvider provider) {
        try {
            final var cardDto = cardRepository.findByToken(cardPaymentRequest.getToken());
            log.info("Found card [{}]", cardDto);

            if (provider.equals(PaymentProvider.BRAINTREE)) {
                log.info("Braintree found as active payment provider");
                return braintreeClientFacade.authorizePayment(cardPaymentRequest, cardDto);
            } else if (provider.equals(PaymentProvider.STRIPE)) {
                log.info("Stripe found as active payment provider");
                final var request =
                        CardPaymentMapper.toStripePaymentRequest(cardPaymentRequest, cardDto);
                log.info("Request mapped to Stripe request [{}]", request);
                PaymentIntentAuthorisationResponse response = stripePaymentIntentClientFacade.authorizePayment(request);
                return CardPaymentMapper.toCardPaymentAuthorisationResponse(response);
            } else {
                // If the flow reaches here, there are no default payment providers set.
                throw new PaymentException("No default card provider could be found", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (EmptyResultException e) {
            log.error("Could not find token [{}]", cardPaymentRequest.getToken(), e);
            throw new TokenException(String.format("Token [%s] does not exist",
                    cardPaymentRequest.getToken()), e);
        } finally {
            // We delete the card Token once this try/catch is over, and that even if an error has been thrown.
            // This is to cut short the lifecycle of a card token and prevent unwanted reuse.
            cardRepository.deleteByToken(cardPaymentRequest.getToken());
        }

    }

}
