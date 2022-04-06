package de.tuclausthal.submissioninterface.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.tuclausthal.submissioninterface.api.oauthconfig.GateClientConfig;
import de.tuclausthal.submissioninterface.api.service.LoginService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Path("/login")
public class LoginController {

    private LoginService loginService;

    public LoginController() {
        this.loginService = new LoginService();
    }

    /**
     * Rest Endpoint to authorize Oauth2.0 client (first step of the device-code flow)
     * POST http://localhost:{port}/{contextPath}/api/login/authorize
     * @param body the payload containing the client details
     * @return a Response Entity containing the credentials the user needs to authorize and get the access credentials
     */
    @POST
    @Path("/authorize")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authorize(String body) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        GateClientConfig gateClientConfig = mapper.readValue(body, GateClientConfig.class);

        return loginService.authorizeClient(gateClientConfig);
    }


    /**
     * Rest Endpoint to get access token and refresh token (second step of the device-code flow)
     * POST http://localhost:{port}/{contextPath}/api/login/token
     * @param body the payload containing the device code gotten in the first step
     * @return a Response Entity containing the access token and refresh token
     */
    @POST
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getToken(String body) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        GateClientConfig gateClientConfig = mapper.readValue(body, GateClientConfig.class);

        return loginService.getToken(gateClientConfig);
    }

    /**
     * Rest Endpoint to refresh the access token
     * GET http://localhost:{port}/{contextPath}/api/login/refresh
     * @return a Response Entity containing the new access and refresh tokens
     */
    @POST
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response refreshToken(String body) throws IOException {
        ObjectNode node = new ObjectMapper().readValue(body, ObjectNode.class);

        return loginService.refreshToken(node);

    }

    /**
     * Rest Endpoint to get the logged in userinfo
     * GET http://localhost:{port}/{contextPath}/api/login/userinfo
     * @return a Response Entity containing the userinfo
     */
    @GET
    @Path("/userinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserinfo(@Context HttpHeaders httpHeaders) throws IOException {

        String bearerToken = httpHeaders.getHeaderString(AUTHORIZATION);
        return loginService.getLoggedInUserInfo(bearerToken);
    }
}