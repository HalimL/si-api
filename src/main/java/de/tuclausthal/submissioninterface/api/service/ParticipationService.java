package de.tuclausthal.submissioninterface.api.service;

import de.tuclausthal.submissioninterface.api.error.ErrorMessage;
import de.tuclausthal.submissioninterface.api.util.HibernateApiSessionManager;
import de.tuclausthal.submissioninterface.persistence.dao.DAOFactory;
import de.tuclausthal.submissioninterface.persistence.dao.UserDAOIf;
import de.tuclausthal.submissioninterface.persistence.datamodel.Participation;
import de.tuclausthal.submissioninterface.persistence.datamodel.Student;
import de.tuclausthal.submissioninterface.persistence.datamodel.User;
import de.tuclausthal.submissioninterface.util.Configuration;
import org.hibernate.Session;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Set;

public class ParticipationService {

    private Session session;
    private UserDAOIf userDAO;

    private String message = "";

    public ParticipationService() {
        this.session = HibernateApiSessionManager.getSession();
        this.userDAO = DAOFactory.UserDAOIf(session);
    }

    public Response getParticipations(int userId) {

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

            return Response
                    .ok()
                    .entity(participationList)
                    .build();
        }

        message = "Sie sind zu keiner Veranstaltung angemeldet, Sie können sich über GATE zu einer Veranstaltung anmelden.";
        ErrorMessage errorMessage = new ErrorMessage(message);

        throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                .entity(errorMessage).build());
    }
}
