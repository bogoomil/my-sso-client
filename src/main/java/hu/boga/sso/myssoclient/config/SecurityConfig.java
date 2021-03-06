package hu.boga.sso.myssoclient.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    public static final String AUTH_ENTRYPOINT_HEADER_NAME = "X-auth-entrypoint";

    private final LogoutSuccessHandler logoutSuccessHandler;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    public SecurityConfig(@Autowired LogoutSuccessHandler logoutSuccessHandler, @Autowired AuthenticationSuccessHandler authenticationSuccessHandler, @Autowired AuthenticationFailureHandler authenticationFailureHandler) {
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        return http
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(ae -> ae
                        .anyExchange()
                        .permitAll()
                )
                .oauth2Login(l -> l
                        .authorizedClientRepository(authorizedClientRepository())
                        .authenticationSuccessHandler(authenticationSuccessHandler)
                        .authenticationFailureHandler(authenticationFailureHandler)
                )
                .logout(l -> l
                        .logoutSuccessHandler(logoutSuccessHandler)
                )
                .csrf()
                .disable()
                .build();
    }
}