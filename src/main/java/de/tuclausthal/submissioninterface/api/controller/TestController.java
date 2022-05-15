package de.tuclausthal.submissioninterface.api.controller;

import de.tuclausthal.submissioninterface.api.error.ErrorMessage;
import de.tuclausthal.submissioninterface.api.oauthconfig.SecureOauth;
import de.tuclausthal.submissioninterface.api.service.TestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

@Path("/tests")
@SecureOauth
public class TestController {

    private TestService testService;

    public TestController() {
        this.testService = new TestService();
    }

    /**
     * Rest Endpoint to execute a test
     * GET http://localhost:{port}/{contextPath}/api/tests/{testId}/user/{userId}
     *
     * @param testId the test id
     * @param userId the user id
     * @return a Response Entity containing the task
     */
    @GET
    @Path("/{testId}/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void executeTest(@PathParam("testId") int testId, @PathParam("userId") int userId, @Suspended AsyncResponse asyncResponse) {

        try {
            asyncResponse.resume(testService.executeTest(testId,userId));
        } catch (ExecutionException | InterruptedException  e) {
            String message = "Something went wrong";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorMessage).build());
        }

        asyncResponse.setTimeout(15000, TimeUnit.MILLISECONDS);
        asyncResponse.setTimeoutHandler(resp -> asyncResponse.resume(
                Response.status(SERVICE_UNAVAILABLE).entity("Timeout").build()));
    }
}
