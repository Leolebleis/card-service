<h1 align="center">Welcome to card-service 👋</h1>

## 🚀 Introduction

This API was built using Micronaut. It relies on a PSQL database to create and store single-use tokens representing card
data. Once a token is created, it can be used to authorize a card payment with a payment provider (either `Stripe`
or `Braintree`).

I am using the Heroku dashboard to gather various Metrics about the application.

## 💻 Run locally

### Requirements

To run this app locally, you will need:

* Java JDK version 14+
* PostgreSQL running on port 5432. The default password is `password` and the default username is `postgres`, but you 
  can change that by accessing [`application.yml`](./src/main/resources/application.yml)

Flyway will attempt to create a `card` table and a `flyway_schema_history` table to keep track of its changes.

### Setup

Once ready, open your terminal and navigate to the root of this repository. You can then execute this command:

```shell
./gradlew clean build
```

Which will build the service. This will resolve all the dependencies and download the packages needed to run this app.

```shell
./gradlew clean run
```

Which should build and start the server on port `8080`. Feel free to navigate
to http://localhost:8080/swagger/card-service-0.1.yml as this will display the swagger specs the server
automatically generated.

You can also run this through your IDE, but note that if you are using IntelliJ you will need to
enable `Annotations processing` in your settings.

### Run the tests

You can run the tests by executing the following command:

```shell
./gradlew test
```

### Example requests

The default API key when running the service locally is `36e0c314-beea-44bf-b737-d255d1f46932`.

Once the service is running on local, you can render the OpenAPI docs using your preferred swagger docs renderer (
eg. [with Firefox you can use this plugin](https://addons.mozilla.org/en-US/firefox/addon/swagger-ui-ff/)), which will
let you send requests to the service directly.

The following request can be used to create a card token:

```curl
curl -X POST "http://localhost:8080/card/token" -H  "accept: application/json" -H  "X-API-KEY: 36e0c314-beea-44bf-b737-d255d1f46932" -H  "Content-Type: application/json" -d "{\"number\":\"4111111111111111\",\"expiryMonth\":\"02\",\"expiryYear\":\"2025\",\"cvc\":\"020\"}"```
```

The following request can be used to authorize a payment:

```curl
curl -X POST "http://localhost:8080/card/payment/authorize" -H  "accept: application/json" -H  "X-PAYMENT-PROVIDER: STRIPE" -H  "X-API-KEY: 36e0c314-beea-44bf-b737-d255d1f46932" -H  "Content-Type: application/json" -d "{\"amount\":\"14.64\",\"currency\":\"gbp\",\"firstName\":\"Bilbo\",\"lastName\":\"Baggins\",\"token\":\"<card token>\"}"
```

By default, this service integrates with `Stripe` using the Stripe API, but also supports integration with Braintree. If
you wish to authorize a payment using Braintree, you should include a `X-PAYMENT-PROVIDER` header with `BRAINTREE` as
the value.

### Card values for testing

Given that this application uses a sandbox for Braintree and Stripe, not all card values will work. Here are a few 
examples of values you can use to test the application:

Successful with both Stripe and Braintree:
* `4111111111111111`

Unsuccessful with Stripe:
* `4000000000009995`, reason is `insufficient_funds`
* `4000000000000101`, reason is `incorrect_cvc`

Unsuccessful with Braintree:
* `4000111111111115`, reason is `Do Not Honor`

## 🔍 Observations

### Braintree

This service uses a braintree sandbox account. Connection with Braintree is done with the Java SDK as opposed to the
GraphQL API as this was quicker to integrate with.

The braintree config values are located in [`application.yml`](./src/main/resources/application.yml), under `braintree`,
and are valid as they point to my sandbox.

Braintree requires to create a Customer in order to create a PaymentMethod, and authorize a payment. This means that we
are effectively making two calls to Braintree through their SDK - one for customer creation, and one for creating the
transaction. At the moment, the application creates a new customer regardless of whether the customer already exists in
Braintree's Vault. An improvement that could be made to this application would be to have a better way of handling
customers.

### Stripe

Integration with Stripe follows a similar pattern to the one I did
here [https://github.com/Leolebleis/integrations-challenge](https://github.com/Leolebleis/integrations-challenge). The
one exception is that I found out that Micronaut does not have an easy way of serializing `x-www-form-urlencoded` data
with custom field names (the Stripe API accepts `snake_case`, whereas the Java POJO fields are `camelCase`).

The stripe config values are located in [`application.yml`](./src/main/resources/application.yml), under `stripe`, and
are valid as they point to my sandbox.

### Heroku and PCI compliance

Heroku has a feature that allows services to run against a cloud
database. [You can read more about it here](https://www.heroku.com/postgres).

The service currently runs on a Hobby dyno and therefore the PSQL database does not have access
to [Heroku shield](https://www.heroku.com/shield), making the DB solution here not PCI-compliant as-is. Nonetheless,
upgrading to Heroku shield should be straightforward.

## 📚 Appendix

### Roadmap

I started development by writing this checklist and went back to it while building the service. It does not represent
all the work done on the service, but is a nice (non-comprehensive) summary of what has been done!

- [x] Build the API config
    - [x] Handle authorization
    - [x] Flyway script to create database
- [x] POST /tokenize endpoint to tokenize card data
    - [x] Store data in Postgres database (PCI-compliant with heroku shield)
    - [x] Custom and graceful errors
- [x] POST /authorize endpoint to authorize a card payment
    - [x] Integrate with Stripe
    - [x] Integrate with Braintree
- [x] Document the API
    - [x] Swagger docs autogenerated through annotations
    - [x] Update README.md and review/add comments
- [x] Deploy the service on heroku
    - [x] Create environment variable such as API key
    - [x] CI through Github

### Links

* [Braintree developer docs](https://developer.paypal.com/braintree/docs)
* [Stripe developer docs](https://stripe.com/docs/api)