package de.tuclausthal.submissioninterface.api.controller;

import de.tuclausthal.submissioninterface.api.oauthconfig.SecureOauth;
import de.tuclausthal.submissioninterface.api.service.TaskService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tasks")
@SecureOauth
public class TaskController {

    private TaskService taskService;

    public TaskController() {
        this.taskService = new TaskService();
    }

    /**
     * Rest Endpoint to get a task by id
     * GET http://localhost:{port}/{contextPath}/api/tasks/{taskId}/user/{userId}
     * @param taskId the task id
     * @param userId the user id
     * @return a Response Entity containing the task
     */
    @GET
    @Path("/{taskId}/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTask(@PathParam("taskId") int taskId, @PathParam("userId") int userId) {
        return  taskService.getTaskByID(taskId, userId);
    }

    /**
     * Rest Endpoint to get tasks by lecture id
     * GET http://localhost:{port}/{contextPath}/api/tasks/lecture/{lectureId}
     * @param lectureId the lecture id
     * @return a Response Entity containing the list of tasks
     */
    @GET
    @Path("/lecture/{lectureId}/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTasks(@PathParam("lectureId") int lectureId) {
        return  taskService.getTasks(lectureId);
    }


    /**
     * Rest Endpoint to get tasks that havent reached their deadline by user id
     * GET http://localhost:{port}/{contextPath}/api/tasks/user/{userId}
     * @param userId the user id
     * @return a Response Entity containing the list of tasks that have'nt reached their deadline
     */
    @GET
    @Path("/user/{userId}/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableTasks(@PathParam("userId") int userId) {
        return  taskService.getAvailableTasks(userId);
    }

    /**
     * Rest Endpoint to get a task by id
     * GET http://localhost:{port}/{contextPath}/api/tasks/{taskId}
     * @param taskId the task id
     * @return a Response Entity containing the task
     */
    @GET
    @Path("/{taskId}")
    @Produces("application/zip")
    public Response getTaskFile(@PathParam("taskId") int taskId) {
        return  taskService.getTaskFile(taskId);
    }
}