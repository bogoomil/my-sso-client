package hu.boga.sso.myssoclient.config;

import java.util.ArrayList;
import java.util.List;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.util.Assert;

import com.nimbusds.jose.util.Base64;

/**
 * A default {@link OidcUserService} működését egészíti ki, az idom-os token
 * szerkezet figyelembe vételével, mivel itt a felhasználói adatokat (jogokat)
 * nem a id_token-ben kapjuk vissza, hanem az accesstokenben.
 *
 * @author kunb
 *
 */
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomOidcUserService.class);

    public static List<? extends GrantedAuthority> getAuthoritiesFromAccessToken(String accessToken, OidcIdToken idToken) {

//        List<GrantedAuthority> l = new ArrayList<>();
//
//        accessToken = accessToken.substring(accessToken.indexOf('.') + 1, accessToken.lastIndexOf('.'));
//
//        Base64 b64 = new Base64(accessToken);
//
//        String at = b64.decodeToString();
//
//        JSONObject json = new JSONObject(at);
//
//        JSONArray jogcsoportok = json.getJSONObject("fiok").getJSONArray("jogcsoport");
//
//        for (int i = 0; i < jogcsoportok.length(); i++) {
//            JSONObject jogcsoport = jogcsoportok.getJSONObject(i);
//
//            JSONArray elemiJogok = jogcsoport.getJSONArray("elemijog");
//            for (int j = 0; j < elemiJogok.length(); j++) {
//                JSONObject elemiJog = elemiJogok.getJSONObject(j);
//                String kod = elemiJog.getString("kod");
//                LOG.debug("ADDING ROLE: {}", "ROLE_" + kod);
//                l.add(new OidcUserAuthority("ROLE_" + kod, idToken, null));
//            }
//
//        }
//        return l;
        return null;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
//        Assert.notNull(userRequest, "userRequest cannot be null");
//        OidcUserInfo userInfo = null;
//
//        List<? extends GrantedAuthority> authorities = null;
//        try {
//            authorities = CustomOidcUserService.getAuthoritiesFromAccessToken(userRequest.getAccessToken().getTokenValue(), userRequest.getIdToken());
//        } catch (JSONException e) {
//            LOG.error(e.getMessage(), e);
//        }
//        return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
        return null;
    }

}