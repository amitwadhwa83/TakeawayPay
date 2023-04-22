package com.takeaway.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@SpringBootApplication
public class TakeawayPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(TakeawayPayApplication.class, args);
    }

    @Bean
    public OpenAPI mortageOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Takeaway Pay application")
                        .description("Backend test for Just Eat Takeaway.com Operations Finance.")
                        .version("v0.0.1")
                        .contact(new Contact()
                                .name("mortgage_help@ing.com"))
                        .license(new License().name("ING 1.0").url("https://www.ing.com.tr/en/knowledge-base/mortgage")));
    }
}
