package com.leolebleis.card.payment.client.braintree;

import com.braintreegateway.*;
import com.leolebleis.card.helpers.errors.payment.PaymentException;
import com.leolebleis.card.payment.helpers.BraintreeProperties;
import com.leolebleis.card.payment.helpers.CardPaymentMapper;
import com.leolebleis.card.payment.model.CardPaymentAuthorizationResponse;
import com.leolebleis.card.payment.model.stripe.CardPaymentRequest;
import com.leolebleis.card.token.model.CardDto;
import io.micronaut.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Singleton
public class BraintreeClientFacade {

    private final BraintreeGateway gateway;

    @Inject
    public BraintreeClientFacade(BraintreeProperties braintreeProperties) {
        // We inject the appropriate Braintree IDs to initialize the SDK.
        this.gateway = new BraintreeGateway(
                Environment.SANDBOX,
                braintreeProperties.getMerchantId(),
                braintreeProperties.getPublicKey(),
                braintreeProperties.getPrivateKey()
        );
    }

    /**
     * Method used to authorize a payment with Braintree.
     *
     * @param cardPaymentRequest The card payment request.
     * @param cardDto            The card associated with the cardToken.
     * @return The Card PaymentAuthorization response.
     */
    public CardPaymentAuthorizationResponse authorizePayment(CardPaymentRequest cardPaymentRequest, CardDto cardDto) {
        var customer =
                this.createCustomer(cardPaymentRequest.getFirstName(), cardPaymentRequest.getLastName(),
                        cardDto.getExpiryYear(), cardDto.getExpiryMonth(), cardDto.getCvc(), cardDto.getNumber());

        return CardPaymentMapper.toCardPaymentAuthorisationResponse(
                this.createTransaction(cardPaymentRequest.getAmount(), customer.getId()));
    }

    /**
     * Method used to create a transaction with Braintree.
     *
     * @param amount     The amount of the transaction.
     * @param customerId The associated Braintree customerId.
     * @return A Braintree Transaction object.
     */
    private Transaction createTransaction(String amount, String customerId) {
        var request = new TransactionRequest()
                .amount(new BigDecimal(amount))
                .customer().done()
                .options().storeInVault(false).done()
                .customerId(customerId);

        Result<Transaction> transactionResult = gateway.transaction().sale(request);

        if (transactionResult.isSuccess()) {
            return transactionResult.getTarget();
        } else {
            // If the transaction is not successfully authorized, we throw a PaymentException with status 402
            // signifying something went wrong with the payment provider.
            throw new PaymentException(String.format("Error authorizing Braintree payment[%s]",
                    transactionResult.getMessage()), HttpStatus.PAYMENT_REQUIRED);
        }
    }

    /**
     * Method used to create a Customer. This is a necessary step to create a PaymentMethod,
     * which will be needed to authorize transactions.
     *
     * @param firstName   The first name.
     * @param lastName    The last name.
     * @param expiryYear  The expiry year of the customer's card.
     * @param expiryMonth The expiry month of the customer's card.
     * @param cvc         The CVC of the customer's card.
     * @param cardNumber  The card number of the customer's card.
     * @return A Braintree Customer object.
     */
    private Customer createCustomer(String firstName, String lastName, String expiryYear,
                                    String expiryMonth, String cvc, String cardNumber) {
        final var customerId = UUID.randomUUID().toString();
        final var customerRequest = new CustomerRequest()
                .id(customerId)
                .firstName(firstName)
                .lastName(lastName)
                .creditCard()
                .expirationYear(expiryYear)
                .expirationMonth(expiryMonth)
                .number(cardNumber)
                .cvv(cvc).options().verifyCard(true).done()
                .done();
        Result<Customer> customerResponse = gateway.customer().create(customerRequest);

        if (customerResponse.isSuccess()) {
            return customerResponse.getTarget();
        } else {
            throw new PaymentException(String.format("Error creating Braintree customer[%s]",
                    customerResponse.getMessage()));
        }

    }

}
