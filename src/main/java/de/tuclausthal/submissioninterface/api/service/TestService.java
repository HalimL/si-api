package de.tuclausthal.submissioninterface.api.service;

import de.tuclausthal.submissioninterface.api.error.ErrorMessage;
import de.tuclausthal.submissioninterface.api.util.HibernateApiSessionManager;
import de.tuclausthal.submissioninterface.authfilter.SessionAdapter;
import de.tuclausthal.submissioninterface.persistence.dao.DAOFactory;
import de.tuclausthal.submissioninterface.persistence.dao.ParticipationDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.SubmissionDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.TestCountDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.TestDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.UserDAOIf;
import de.tuclausthal.submissioninterface.persistence.datamodel.Participation;
import de.tuclausthal.submissioninterface.persistence.datamodel.Submission;
import de.tuclausthal.submissioninterface.persistence.datamodel.Task;
import de.tuclausthal.submissioninterface.persistence.datamodel.Test;
import de.tuclausthal.submissioninterface.persistence.datamodel.User;
import de.tuclausthal.submissioninterface.testframework.TestExecutor;
import de.tuclausthal.submissioninterface.testframework.tests.TestTask;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.concurrent.ExecutionException;


public class TestService {

    private Session session;
    private UserDAOIf userDAO;
    private TestDAOIf testDAO;
    private ParticipationDAOIf participationDAO;
    private SubmissionDAOIf submissionDAO;
    private TestCountDAOIf testCountDAO;

    private String message = "";

    public TestService() {
        this.session = HibernateApiSessionManager.getSession();
        this.userDAO = DAOFactory.UserDAOIf(session);
        this.testDAO = DAOFactory.TestDAOIf(session);
        this.participationDAO = DAOFactory.ParticipationDAOIf(session);
        this.submissionDAO = DAOFactory.SubmissionDAOIf(session);
        this.testCountDAO = DAOFactory.TestCountDAOIf(session);
    }

    public Response executeTest(int testId, int userId) throws ExecutionException, InterruptedException {

        Test test = testDAO.getTest(testId);

        if (test == null) {
            message = "Test nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        Task task = test.getTask();
        User user = userDAO.getUser(userId);

        if (user == null) {
            message = "User nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);

            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        // check Lecture Participation
        Participation participation = participationDAO.getParticipation(user, task.getTaskGroup().getLecture());
        if (participation == null || test.getTimesRunnableByStudents() == 0) {
            message = "insufficient rights";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(errorMessage).build());
        }

        Submission submission = submissionDAO.getSubmission(task, user);
        if (submission == null) {
            message = "Abgabe nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        if ((task.getDeadline().before(new Date()) || (task.isAllowPrematureSubmissionClosing() && submission.isClosed()))) {
            message = "Testen nicht mehr möglich";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage).build());
        }

        if (testCountDAO.canStillRunXTimes(test, submission) == 0) {
            message = "Dieser Test kann nicht mehr ausgeführt werden. Limit erreicht.";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage).build());
        }

        Transaction tx = session.beginTransaction();
        session.buildLockRequest(LockOptions.UPGRADE).lock(submission);
        SessionAdapter.QueuedTest resultFuture = new SessionAdapter.QueuedTest(test.getId(), submission.getLastModified(), TestExecutor.executeTask(new TestTask(test, submission)));
        testCountDAO.canSeeResultAndIncrementCounterTransaction(test, submission);
        tx.commit();


        return Response
                .ok()
                .entity(resultFuture.testResult.get())
                .build();

    }
}