package hu.boga.sso.myssoclient.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


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

    /**
     * Most of the spring security classes that store data are already implemented using WebSession.
     * The only one that is not is {@link ServerOAuth2AuthorizedClientRepository} so we define that bean ourselves.
     * @return ServerOAuth2AuthorizedClientRepository
     */
    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

//    @Bean
//    public DefaultAuthorizationCodeTokenResponseClient tokenResponseClient(){
//
//        return new DefaultAuthorizationCodeTokenResponseClient();
//    }
//
//
//    @Bean
//    CustomOidcUserService userService(){
//        return new CustomOidcUserService();
//    }
////
//    @Bean
//    AuthenticationProvider authenticationProvider(){
//        OAuth2UserService<OidcUserRequest, OidcUser> userService;
//        return new OidcAuthorizationCodeAuthenticationProvider(tokenResponseClient(), userService());
//    }

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

    static {
        disableSslVerification();
    }

    private static void disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            } };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

}