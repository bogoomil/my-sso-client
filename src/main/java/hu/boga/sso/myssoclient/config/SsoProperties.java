package hu.boga.sso.myssoclient.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sso")
public class SsoProperties implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(SsoProperties.class);

    private String sessionCookieName;
    private String allowedCorsOrigin;
    private String loginUri;
    private String rolesClaim;
    private String nameClaim;

    public String getSessionCookieName() {
        return sessionCookieName;
    }

    public void setSessionCookieName(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }

    public String getAllowedCorsOrigin() {
        return allowedCorsOrigin;
    }

    public void setAllowedCorsOrigin(String allowedCorsOrigin) {
        this.allowedCorsOrigin = allowedCorsOrigin;
    }

    public String getLoginUri() {
        return loginUri;
    }

    public void setLoginUri(String loginUri) {
        this.loginUri = loginUri;
    }

    public String getRolesClaim() {
        return this.rolesClaim;
    }

    public void setRolesClaim(String rolesClaim) {
        this.rolesClaim = rolesClaim;
    }

    public String getNameClaim() {
        return nameClaim;
    }

    public void setNameClaim(String nameClaim) {
        this.nameClaim = nameClaim;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean isOk = true;
        if (this.loginUri == null) {
            LOG.error("property sso.login-uri must be provided");
            isOk = false;
        }
        if (this.allowedCorsOrigin == null) {
            LOG.warn("property sso.allowed-cors-origin should be provided, currently http://localhost:8081 is used");
            this.allowedCorsOrigin = "http://localhost:8081";
        }
        if (this.rolesClaim == null) {
            LOG.error("property sso.roles-claim must be provided");
            isOk = false;
        }
        if (this.nameClaim == null) {
            LOG.error("property sso.name-claim must be provided");
            isOk = false;
        }
        if (this.sessionCookieName == null) {
            LOG.warn("property sso.session-cookie-name should be provided, currently SESSION is used");
            this.sessionCookieName = "SESSION";
        }
        if (!isOk) {
            throw new InsufficientConfigError();
        }
    }

    private class InsufficientConfigError extends RuntimeException {
    }
}
