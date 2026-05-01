package io.vyndra.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI documentation configuration for the order service.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI metadata shown in the Swagger UI.
     *
     * @return the OpenAPI descriptor
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .version("1.0.0")
                        .description("REST API for managing orders in the vyndra platform")
                        .contact(new Contact()
                                .name("Vyndra Engineering")
                                .email("engineering@vyndra.io")));
    }
}
