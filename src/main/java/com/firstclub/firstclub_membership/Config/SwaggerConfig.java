package com.firstclub.firstclub_membership.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FirstClub Membership API")
                        .description("Backend system for the FirstClub tiered membership program. " +
                                "Supports Monthly/Quarterly/Yearly plans with Silver/Gold/Platinum tiers, " +
                                "configurable benefits, automatic tier upgrades, and full audit trail.")
                        .version("1.0.0")
                        .contact(new Contact().name("FirstClub Engineering")));
    }
}
