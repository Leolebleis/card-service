package com.leolebleis.card;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Card Service",
                version = "0.1",
                description = "A card tokenization and payment service built in Micronaut.",
                contact = @Contact(url = "https://leolebleis.com", name = "Leo Le Bleis", email = "leo.lebleis@gmail.com")
        ),
        servers = {
                @Server(description = "local", url = "http://localhost:8080"),
                @Server(description = "live", url = "https://card-service.herokuapp.com")
        }
)
@SecurityScheme(name = "apiKey",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-API-KEY",
        description = "Unique API key to authenticate your requests.")
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
