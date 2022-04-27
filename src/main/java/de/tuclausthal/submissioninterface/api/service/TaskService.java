package de.tuclausthal.submissioninterface.api.service;

import de.tuclausthal.submissioninterface.api.error.ErrorMessage;
import de.tuclausthal.submissioninterface.api.util.HibernateApiSessionManager;
import de.tuclausthal.submissioninterface.persistence.dao.DAOFactory;
import de.tuclausthal.submissioninterface.persistence.dao.LectureDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.ParticipationDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.TaskDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.UserDAOIf;
import de.tuclausthal.submissioninterface.persistence.datamodel.Lecture;
import de.tuclausthal.submissioninterface.persistence.datamodel.Participation;
import de.tuclausthal.submissioninterface.persistence.datamodel.ParticipationRole;
import de.tuclausthal.submissioninterface.persistence.datamodel.Student;
import de.tuclausthal.submissioninterface.persistence.datamodel.Task;
import de.tuclausthal.submissioninterface.persistence.datamodel.TaskGroup;
import de.tuclausthal.submissioninterface.persistence.datamodel.User;
import de.tuclausthal.submissioninterface.util.Configuration;
import de.tuclausthal.submissioninterface.util.Util;
import org.hibernate.Session;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskService {

    private Session session;
    private UserDAOIf userDAO;
    private TaskDAOIf taskDAO;
    private LectureDAOIf lectureDAO;
    private ParticipationDAOIf participationDAO;

    private String message = "";

    public TaskService() {
        this.session = HibernateApiSessionManager.getSession();
        this.userDAO = DAOFactory.UserDAOIf(session);
        this.taskDAO = DAOFactory.TaskDAOIf(session);
        this.lectureDAO = DAOFactory.LectureDAOIf(session);
        this.participationDAO = DAOFactory.ParticipationDAOIf(session);
    }

    public Response getTaskByID(int taskId, int userId) {
        Task task = taskDAO.getTask(taskId);
        User user = userDAO.getUser(userId);


        if (task == null) {
            message = "Aufgabe nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        Participation participation = participationDAO.getParticipation(user, task.getTaskGroup().getLecture());

        if (participation == null) {
            if (task.getTaskGroup().getLecture().getSemester() == Util.getCurrentSemester()) {
                message = "Sie versuchen auf eine Veranstaltung zuzugreifen, für die Sie nicht angemeldet sind";
                ErrorMessage errorMessage = new ErrorMessage(message);
                throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                        .entity(errorMessage).build());
            }

            message = "Sie versuchen auf eine Veranstaltung zuzugreifen, für die Sie nicht angemeldet sind";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(errorMessage).build());
        }

        if (task.getStart().after(new Date()) && participation.getRoleType().compareTo(ParticipationRole.TUTOR) < 0) {
            message = "Aufgabe nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);

            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        return Response
                .ok()
                .entity(task)
                .build();
    }

    public Response getTasks(int lectureId) {

        Lecture lecture = lectureDAO.getLecture(lectureId);

        if (lecture == null) {
            message = "Vorlesung nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        List<Task> tasks = taskDAO.getTasks(lecture, true);

        if (tasks.isEmpty()) {
            message = "Diese Vorlesung hat (noch) keine Aufgaben";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        return Response
                .ok()
                .entity(tasks)
                .build();

    }


    public Response getAvailableTasks(int userId) {

        User user = userDAO.getUser(userId);

        if (user == null) {
            message = "User nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);

            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        if (Configuration.getInstance().isMatrikelNumberMustBeEnteredManuallyIfMissing() && !(user instanceof Student)) {
            message = "Bitte loggen Sie sich bei GATE ein und geben Sie Ihre Matrikelnummer ein";
            ErrorMessage errorMessage = new ErrorMessage(message);

            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        if (user instanceof Student) {
            Student student = (Student) user;

            if (student.getStudiengang() == null) {
                message = "Bitte loggen Sie sich bei GATE ein und geben Sie Ihren Studiengang ein";
                ErrorMessage errorMessage = new ErrorMessage(message);

                throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                        .entity(errorMessage).build());
            }
        }

        if (!user.getLectureParticipant().isEmpty()) {

            Set<Participation> participationList = user.getLectureParticipant();

            List<Task> availableTasks = participationList.stream()
                    .map(Participation::getLecture)
                    .map(Lecture::getTaskGroups)
                    .flatMap(Collection::stream)
                    .map(TaskGroup::getTasks)
                    .flatMap(Collection::stream)
                    .filter(task -> task.getStart().before(new Date()))
                    .filter(task -> task.getDeadline().after(new Date()))
                    .filter(task -> !task.isSCMCTask())
                    .collect(Collectors.toList());

            return Response
                    .ok()
                    .entity(availableTasks)
                    .build();
        }

        message = "Sie sind zu keiner Veranstaltung angemeldet, Sie können sich über GATE zu einer Veranstaltung anmelden.";
        ErrorMessage errorMessage = new ErrorMessage(message);

        throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                .entity(errorMessage).build());
    }



    public Response getTaskFile(int taskId) {

        Task task = taskDAO.getTask(taskId);

        if (task == null) {
            message = "Aufgabe nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);

            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        final File taskFile = new File(Configuration.getInstance().getDataPath().getAbsolutePath()
                + System.getProperty("file.separator") + task.getTaskGroup().getLecture().getId()
                + System.getProperty("file.separator") + task.getTaskid()
                + System.getProperty("file.separator") + task.getFeaturedFiles());


        if (!taskFile.exists()) {
            message = "Datei/Pfad nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);

            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        return Response
                .ok()
                .entity(taskFile)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= " + taskFile.getName())
                .build();
    }


}