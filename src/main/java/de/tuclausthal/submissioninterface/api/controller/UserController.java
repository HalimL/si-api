package de.tuclausthal.submissioninterface.api.controller;

import de.tuclausthal.submissioninterface.api.oauthconfig.SecureOauth;
import de.tuclausthal.submissioninterface.api.service.UserService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@SecureOauth
public class UserController {

    private UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    /**
     * Rest Endpoint to get a particular user by id
     * GET http://localhost:{port}/{contextPath}/api/users/{userId}
     * @param userId the user id
     * @return a Response Entity containing the user
     */
    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("userId") int userId) {
        return userService.getUserById(userId);
    }

    /**
     * Rest Endpoint to get a particular user by username
     * GET http://localhost:{port}/{contextPath}/api/users/username/{username}
     * @param username the username
     * @return a Response Entity containing the user
     */
    @GET
    @Path("/username/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByUsername(@PathParam("username") String username) {
        return userService.getUserByUsername(username);
    }


    /**
     * Rest Endpoint to get a particular user by email
     * GET http://localhost:{port}/{contextPath}/api/users/email/{email}
     * @param email the email
     * @return a Response Entity containing the user
     */
    @GET
    @Path("/email/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByEmail(@PathParam("email") String email) {
        return userService.getUserByEmail(email);
    }
}