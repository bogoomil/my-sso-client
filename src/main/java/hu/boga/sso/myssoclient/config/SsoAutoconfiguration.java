package hu.boga.sso.myssoclient.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableConfigurationProperties(SsoProperties.class)
@EnableRedisRepositories
public class SsoAutoconfiguration {

    @Autowired
    private SsoProperties properties;

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



