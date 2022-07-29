package hu.boga.sso.myssoclient.controller;

import hu.boga.sso.myssoclient.config.SecurityConfig;
import hu.boga.sso.myssoclient.config.SsoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth/whoami")
public class WhoAmIController {

    private final SsoProperties properties;

    public WhoAmIController(@Autowired SsoProperties properties) {
        this.properties = properties;
    }

    @GetMapping()
    private Mono<ResponseEntity<Map<String, Object>>> whoami(ServerWebExchange exchange) {

        return exchange
                .getPrincipal()
                .cast(Authentication.class)
                .defaultIfEmpty(new EmptyAuthentication())
                .map(p -> {
                    if (p.isAuthenticated()) {
                        return new ResponseEntity<>(((OAuth2AuthenticationToken) p).getPrincipal().getAttributes(), HttpStatus.OK);
                    } else {
                        exchange.getResponse().getHeaders().add(SecurityConfig.AUTH_ENTRYPOINT_HEADER_NAME, properties.getLoginUri());
                        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
                    }
                });
    }
}