package hu.boga.sso.myssoclient.filter;

import hu.boga.sso.myssoclient.config.SecurityConfig;
import hu.boga.sso.myssoclient.config.SsoProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;


@Configuration
public class Global401Filter {

    private final SsoProperties properties;

    public Global401Filter(SsoProperties properties) {
        this.properties = properties;
    }

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                            ServerHttpResponse response = exchange.getResponse();
                            if (HttpStatus.UNAUTHORIZED.equals(response.getStatusCode())) {
                                response.getHeaders().add(SecurityConfig.AUTH_ENTRYPOINT_HEADER_NAME, properties.getLoginUri());
                            }
                        })
                );
    }

}