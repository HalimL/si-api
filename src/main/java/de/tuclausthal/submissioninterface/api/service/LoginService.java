package de.tuclausthal.submissioninterface.api.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.tuclausthal.submissioninterface.api.error.ErrorMessage;
import de.tuclausthal.submissioninterface.api.oauthconfig.GateClientConfig;
import de.tuclausthal.submissioninterface.util.Configuration;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.ConnectException;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

public class LoginService {

    public static final String BEARER = "Bearer";
    public static final String TOKEN_ENDPOINT = Configuration.getInstance().getOauth2RealmUrl() + "/protocol/openid-connect/token";
    public static final String AUTHORIZATIOPN_ENDPOINT = Configuration.getInstance().getOauth2RealmUrl() + "/protocol/openid-connect/auth/device";
    private final OkHttpClient client;
    private final MediaType mediaType;

    public LoginService() {
        client = new OkHttpClient().newBuilder().build();
        mediaType = MediaType.parse("application/x-www-form-urlencoded");
    }

    public Response authorizeClient(GateClientConfig gateClientConfig) throws IOException {
        RequestBody requestBody = RequestBody.create("client_id=" + gateClientConfig.getClientId() + "&scope=" + gateClientConfig.getScope(), mediaType);
        Request request = buildPostRequest(AUTHORIZATIOPN_ENDPOINT, requestBody);
        return getResponse(request);
    }

    public Response getToken(GateClientConfig gateClientConfig) throws IOException {
        RequestBody requestBody = RequestBody.create("client_id=" + gateClientConfig.getClientId() + "&device_code=" + gateClientConfig.getDeviceCode() + "&grant_type=" + gateClientConfig.getDeviceCodeGrantType(), mediaType);
        Request request = buildPostRequest(TOKEN_ENDPOINT, requestBody);
        return getResponse(request);
    }

    public Response refreshToken(ObjectNode jsonBody) throws IOException {
        String clientId = jsonBody.path("clientId").asText();
        String refreshToken = jsonBody.path("refreshToken").asText();
        String grantType = jsonBody.path("grantType").asText();

        RequestBody requestBody = RequestBody.create("client_id=" + clientId + "&grant_type=" + grantType + "&refresh_token=" + refreshToken, mediaType);
        Request request = buildPostRequest(TOKEN_ENDPOINT, requestBody);
        return getResponse(request);
    }

    public Response getLoggedInUserInfo(String bearerToken) throws IOException {
        if (bearerToken == null || !bearerToken.startsWith(BEARER)) {
            String message = "No valid bearer token provided";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorMessage).build());
        }

        Request request = new Request.Builder()
                .url(Configuration.getInstance().getOauth2RealmUrl() + "/protocol/openid-connect/userinfo")
                .addHeader(AUTHORIZATION, bearerToken)
                .build();

        return getResponse(request);
    }

    public Response getResponse(Request request) throws IOException {
        okhttp3.Response clientResponse;

        try {
            clientResponse = client.newCall(request).execute();
        } catch (ConnectException e) {

            String message = "Der Authorization Server ist nicht gestartet";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(errorMessage).build());
        }

        int statusCode = clientResponse.code();
        String response = clientResponse.body().string();

        if (statusCode != 200) {
            throw new WebApplicationException(Response.status(statusCode)
                    .entity(response).build());
        }
        return Response
                .ok()
                .entity(response)
                .build();
    }

    public Request buildPostRequest(String url, RequestBody requestBody) {
        return new Request.Builder()
                .url(url)
                .method("POST", requestBody)
                .addHeader(CONTENT_TYPE, "application/x-www-form-urlencoded")
                .build();
    }

}