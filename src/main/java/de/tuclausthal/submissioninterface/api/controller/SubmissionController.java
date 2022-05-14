package de.tuclausthal.submissioninterface.api.controller;

import de.tuclausthal.submissioninterface.api.oauthconfig.SecureOauth;
import de.tuclausthal.submissioninterface.api.service.SubmissionService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/submissions")
@SecureOauth
public class SubmissionController {

    private SubmissionService submissionService;

    public SubmissionController() {
        this.submissionService = new SubmissionService();
    }

    /**
     * Rest Endpoint to get a submission by id
     * GET http://localhost:{port}/{contextPath}/api/submissions/{submissionId}/task/{taskId}/user/{userId}
     * @param submissionId the submission id
     * @param taskId the task id
     * @param userId the user id
     * @return a Response Entity containing the submission
     */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{submissionId}/task/{taskId}/user/{userId}")
    public Response getSubmissionById(@PathParam("submissionId") int submissionId, @PathParam("taskId") int taskId, @PathParam("userId") int userId) {
        return submissionService.getSubmissionById(submissionId, taskId, userId);
    }

    /**
     * Rest Endpoint to get all submissions for a participation
     * GET http://localhost:{port}/{contextPath}/api/submissions/participation/{participationId}
     * @param participationId the participation id
     * @return a Response Entity containing the list of submission
     */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/participation/{participationId}")
    public Response getAllSubmissionByParticipationId(@PathParam("participationId") int participationId) {
        return submissionService.getSubmissions(participationId);
    }

    /**
     * Rest Endpoint to get the user submission for a task
     * GET http://localhost:{port}/{contextPath}/api/submissions/task/{taskId}/user/{userId}
     * @param taskId the task id
     * @param userId the user id
     * @return a Response Entity containing the user submission for a task
     */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("task/{taskId}/user/{userId}")
    public Response getSubmissionForTaskByUserId(@PathParam("taskId") int taskId, @PathParam("userId") int userId) {
        return submissionService.getUserSubmissionForTask(taskId, userId);
    }


    /**
     * Rest Endpoint to submit a submission for a task
     * POST http://localhost:{port}/{contextPath}/api/submissions/task/{taskId}/user/{userId}
     * @param taskId the task id
     * @param userId the user id
     * @param request the HttpServlet request context
     * @return a Response Entity saying if the submission was successful or not
     */

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/task/{taskId}/user/{userId}")
    public Response submitSubmission(@PathParam("taskId") int taskId, @PathParam("userId") int userId, @Context HttpServletRequest request) throws IOException, ServletException {
        return submissionService.submitSubmission(taskId, userId, request);
    }

}