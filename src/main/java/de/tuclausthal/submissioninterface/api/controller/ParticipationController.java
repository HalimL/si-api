package de.tuclausthal.submissioninterface.api.controller;

import de.tuclausthal.submissioninterface.api.oauthconfig.SecureOauth;
import de.tuclausthal.submissioninterface.api.service.ParticipationService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/participations")
@SecureOauth
public class ParticipationController {

    private ParticipationService participationService;

    public ParticipationController() {
        this.participationService = new ParticipationService();
    }

    /**
     * Rest Endpoint to get a particular lecture by id
     * GET http://localhost:{port}/{contextPath}/api/participations/user/{userId}
     * @param userId the user id
     * @return a Response Entity containing the users participations
     */
    @GET
    @Path("/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getParticipations( @PathParam("userId") int userId) {
        return participationService.getParticipations(userId);
    }
}
