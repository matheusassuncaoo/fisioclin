package com.br.fasipe.fisioclin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${fisioclin.openapi.dev-url:http://localhost:8080}")
    private String devUrl;

    @Value("${fisioclin.openapi.prod-url:https://fisioclin.com.br}")
    private String prodUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL em ambiente de desenvolvimento");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL em ambiente de produção");

        Contact contact = new Contact();
        contact.setEmail("suporte@fisioclin.com.br");
        contact.setName("FisioClin");
        contact.setUrl("https://www.fisioclin.com.br");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("API FisioClin")
                .version("1.0")
                .contact(contact)
                .description("API REST para Sistema de Gerenciamento de Clínica de Fisioterapia")
                .termsOfService("https://www.fisioclin.com.br/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}
