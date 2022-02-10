package hu.boga.sso.myssoclient.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * When authentication process fails we redirect user to "/" with feedback in `error` query parameter to inform user what happened.
 */
@Component
public class AuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    private ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        URI location = UriComponentsBuilder.fromUriString("/").query("error=" + exception.getMessage()).build().toUri();

        return redirectStrategy.sendRedirect(webFilterExchange.getExchange(), location);
    }
}