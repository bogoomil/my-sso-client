package hu.boga.sso.myssoclient.customization;

//import org.springframework.security.oauth2.client.endpoint.AbstractWebClientReactiveOAuth2AccessTokenResponseClient;
//import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
//import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
//import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
//import org.springframework.web.reactive.function.BodyInserters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.web.reactive.function.OAuth2BodyExtractors;
import org.springframework.util.*;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

public class MyReactiveAuthorizationCodeTokenResponseClient extends
        WebClientReactiveAuthorizationCodeTokenResponseClient {

    private WebClient webClient = WebClient.builder().build();

    private Converter<OAuth2AuthorizationCodeGrantRequest, HttpHeaders> headersConverter = this::populateTokenRequestHeaders;

    private BodyExtractor<Mono<OAuth2AccessTokenResponse>, ReactiveHttpInputMessage> bodyExtractor = OAuth2BodyExtractors
            .oauth2AccessTokenResponse();

    private Converter<OAuth2AuthorizationCodeGrantRequest, MultiValueMap<String, String>> parametersConverter = this::populateTokenRequestParameters;



    ClientRegistration clientRegistration(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        return grantRequest.getClientRegistration();
    }


    @Override
    public Mono<OAuth2AccessTokenResponse> getTokenResponse(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        Assert.notNull(grantRequest, "grantRequest cannot be null");
        // @formatter:off
        return Mono.defer(() -> this.webClient.post()
                .uri(clientRegistration(grantRequest).getProviderDetails().getTokenUri())
                .headers((headers) -> {
                    HttpHeaders headersToAdd = headersConverter.convert(grantRequest);
                    if (headersToAdd != null) {
                        headers.addAll(headersToAdd);
                    }
                })
                .body(createTokenRequestBody(grantRequest))
                .exchange()
                .flatMap((response) -> readTokenResponse(grantRequest, response))
        );

    }

    private HttpHeaders populateTokenRequestHeaders(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        HttpHeaders headers = new HttpHeaders();
        ClientRegistration clientRegistration = clientRegistration(grantRequest);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(clientRegistration.getClientAuthenticationMethod())
                || ClientAuthenticationMethod.BASIC.equals(clientRegistration.getClientAuthenticationMethod())) {
            String clientId = encodeClientCredential(clientRegistration.getClientId());
            String clientSecret = encodeClientCredential(clientRegistration.getClientSecret());
            headers.setBasicAuth(clientId, clientSecret);
        }
        return headers;
    }

    private static String encodeClientCredential(String clientCredential) {
        try {
            return URLEncoder.encode(clientCredential, StandardCharsets.UTF_8.toString());
        }
        catch (UnsupportedEncodingException ex) {
            // Will not happen since UTF-8 is a standard charset
            throw new IllegalArgumentException(ex);
        }
    }

    private BodyInserters.FormInserter<String> createTokenRequestBody(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        MultiValueMap<String, String> parameters = parametersConverter.convert(grantRequest);
        return populateTokenRequestBody(grantRequest, BodyInserters.fromFormData(parameters));
    }

    BodyInserters.FormInserter<String> populateTokenRequestBody(OAuth2AuthorizationCodeGrantRequest grantRequest,
                                                                BodyInserters.FormInserter<String> body) {
        ClientRegistration clientRegistration = clientRegistration(grantRequest);
        if (!ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(clientRegistration.getClientAuthenticationMethod())
                && !ClientAuthenticationMethod.BASIC.equals(clientRegistration.getClientAuthenticationMethod())) {
            body.with(OAuth2ParameterNames.CLIENT_ID, clientRegistration.getClientId());
        }
        if (ClientAuthenticationMethod.CLIENT_SECRET_POST.equals(clientRegistration.getClientAuthenticationMethod())
                || ClientAuthenticationMethod.POST.equals(clientRegistration.getClientAuthenticationMethod())) {
            body.with(OAuth2ParameterNames.CLIENT_SECRET, clientRegistration.getClientSecret());
        }
        Set<String> scopes = scopes(grantRequest);
        if (!CollectionUtils.isEmpty(scopes)) {
            body.with(OAuth2ParameterNames.SCOPE, StringUtils.collectionToDelimitedString(scopes, " "));
        }
        return body;
    }

    private Mono<OAuth2AccessTokenResponse> readTokenResponse(OAuth2AuthorizationCodeGrantRequest grantRequest, ClientResponse response) {
        return response.body(this.bodyExtractor)
                .map((tokenResponse) -> populateTokenResponse(grantRequest, tokenResponse));
    }

    OAuth2AccessTokenResponse populateTokenResponse(OAuth2AuthorizationCodeGrantRequest grantRequest, OAuth2AccessTokenResponse tokenResponse) {
        if (CollectionUtils.isEmpty(tokenResponse.getAccessToken().getScopes())) {
            Set<String> defaultScopes = defaultScopes(grantRequest);
            // @formatter:off
            tokenResponse = OAuth2AccessTokenResponse
                    .withResponse(tokenResponse)
                    .scopes(defaultScopes)
                    .build();
            // @formatter:on
        }
        return tokenResponse;
    }


    Set<String> scopes(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        return Collections.emptySet();
    }


    Set<String> defaultScopes(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        return grantRequest.getAuthorizationExchange().getAuthorizationRequest().getScopes();
    }
    private MultiValueMap<String, String> populateTokenRequestParameters(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add(OAuth2ParameterNames.GRANT_TYPE, grantRequest.getGrantType().getValue());
        return parameters;
    }


}
