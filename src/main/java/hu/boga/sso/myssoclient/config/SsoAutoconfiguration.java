package hu.boga.sso.myssoclient.config;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Configuration
@EnableConfigurationProperties(SsoProperties.class)
@EnableRedisRepositories
public class SsoAutoconfiguration {

    @Autowired
    private SsoProperties properties;


//    @Bean
//    @ConditionalOnMissingBean(ConfigurableJWTProcessor.class)
//    ConfigurableJWTProcessor<SecurityContext> jwtProcessor() throws MalformedURLException {
//        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
//        jwtProcessor.setJWSTypeVerifier(
//                new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("JWT")));
//        JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(properties.getJwksUri()));
//        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
//        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);
//        jwtProcessor.setJWSKeySelector(keySelector);
//        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
//                new JWTClaimsSet.Builder().issuer(properties.getJwksIssuerUri()).build(),
//                new HashSet<>(Arrays.asList("jti", "exp", "iat", "iss", "aud", "sub", "typ", "azp"))));
//        return jwtProcessor;
//    }

    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        resolver.setCookieName(properties.getSessionCookieName());
        resolver.addCookieInitializer(builder -> builder.path("/"));
        resolver.addCookieInitializer(builder -> builder.sameSite("Lax"));
        resolver.addCookieInitializer(builder -> builder.secure(true));
        resolver.addCookieInitializer(builder -> builder.httpOnly(true));

        return resolver;
    }

//    @Bean
//    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
//
//        return routeLocatorBuilder
//                .routes()
//                .route(p -> p
//                        .path("/")
//                        .filters(f -> f.filter(homeFilter().apply(
//                                new HomeGatewayFilterFactory.Config())))
//                        .uri("forward:/index.html")
//                )
//                .route(p -> p
//                        .path("/auth/login")
//                        .filters(f -> f.filter(loginFilter().apply(
//                                new RedirectToSsoLoginGatewayFilterFactory.Config(
//                                        properties.getClientId(),
//                                        properties.getRedirectUri(),
//                                        properties.getLoginUri()))))
//                        .uri(properties.getLoginUri())
//                )
//                .route(p -> p
//                        .path("/auth/logout")
//                        .filters(f -> f.filter(logoutGatewayFilterFactory().apply(
//                                new LogoutGatewayFilterFactory.Config())))
//                        .uri("forward:/")
//                )
//                .route(p -> p
//                        .path("/auth/redirectin")
//                        .filters(f -> f.filter(redirectInGatewayFilterFactory().apply(
//                                new RedirectInGatewayFilterFactory.Config())))
//                        .uri("forward:/")
//                )
//                .build();
//    }

    @Bean
    public CorsWebFilter corsWebFilter() {

        final CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Collections.singletonList(properties.getAllowedCorsOrigin()));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        corsConfig.addAllowedHeader("*");

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}



