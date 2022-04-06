package de.tuclausthal.submissioninterface.api.oauthconfig;

import de.tuclausthal.submissioninterface.util.Configuration;

public class GateClientConfig {

    private String clientId;
    private String scope;
    private String deviceCode;
    private final String deviceCodeGrantType = Configuration.getInstance().getOuath2DeviceCodeGrantType();

    public GateClientConfig(String clientId, String scope, String deviceCode) {
        this.clientId = clientId;
        this.scope = scope;
        this.deviceCode = deviceCode;
    }

    public GateClientConfig() {}

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceCodeGrantType() {
        return deviceCodeGrantType;
    }

}
