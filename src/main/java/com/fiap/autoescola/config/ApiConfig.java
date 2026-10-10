package com.fiap.autoescola.config;

import java.time.*;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
@Configuration
public class ApiConfig {
    @Bean public Clock clock(){return Clock.system(ZoneId.of("America/Sao_Paulo"));}
    @Bean public OpenAPI openAPI(){
        return new OpenAPI().info(new Info().title("Auto-Escola — Checkpoint 5").version("2.0.0")
            .description("Evolução do CP4: JWT, CRUD, agenda, ViaCEP, Swagger e CORS. Datas no fuso de São Paulo."))
            .components(new Components().addSecuritySchemes("bearerAuth",new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
    @Bean public RestClient viaCepClient(@Value("${app.viacep.base-url}") String url){
        var factory=new SimpleClientHttpRequestFactory();factory.setConnectTimeout(Duration.ofSeconds(3));factory.setReadTimeout(Duration.ofSeconds(5));
        return RestClient.builder().baseUrl(url).requestFactory(factory).build();
    }
}
