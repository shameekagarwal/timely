package com.timely.projectservice.config;

import java.util.Arrays;
import java.util.List;

import com.timely.projectservice.constant.ProfilesConstants;
import com.timely.projectservice.filter.FirebaseFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@Profile({ ProfilesConstants.PROD, ProfilesConstants.DEV, ProfilesConstants.QA })
public class SpringfoxConfig {

    private static final String JWT_KEY_NAME = "jwt";

    @Bean
    public Docket api() {
        ApiKey apiKey = new ApiKey(JWT_KEY_NAME, FirebaseFilter.HEADER_STRING, "header");
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = Arrays
                .asList(new SecurityReference(JWT_KEY_NAME, authorizationScopes));
        SecurityContext securityContext = SecurityContext.builder().securityReferences(securityReferences).build();
        List<SecurityContext> securityContexts = Arrays.asList(securityContext);
        return new Docket(DocumentationType.SWAGGER_2).securityContexts(securityContexts)
                .securitySchemes(Arrays.asList(apiKey)).select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build();
    }

}
