package de.tuclausthal.submissioninterface.api.controller;

import de.tuclausthal.submissioninterface.api.oauthconfig.SecureOauth;
import de.tuclausthal.submissioninterface.api.service.LectureService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/lectures")
@SecureOauth
public class LectureController {

    private LectureService lectureService;

    public LectureController() {
        this.lectureService = new LectureService();
    }

    /**
     * Rest Endpoint to get a particular lecture by id
     * GET http://localhost:{port}/{contextPath}/api/lectures/{lectureId}/user/{userId}
     * @param lectureId the lecture id
     * @param userId the user id
     * @return a Response Entity containing the lecture
     */
    @GET
    @Path("{lectureId}/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLecture(@PathParam("lectureId") int lectureId, @PathParam("userId") int userId) {
        return lectureService.getLecture(lectureId, userId);
    }
}