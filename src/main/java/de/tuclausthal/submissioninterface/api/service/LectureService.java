package de.tuclausthal.submissioninterface.api.service;

import de.tuclausthal.submissioninterface.api.error.ErrorMessage;
import de.tuclausthal.submissioninterface.api.util.HibernateApiSessionManager;
import de.tuclausthal.submissioninterface.persistence.dao.DAOFactory;
import de.tuclausthal.submissioninterface.persistence.dao.LectureDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.ParticipationDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.UserDAOIf;
import de.tuclausthal.submissioninterface.persistence.datamodel.Lecture;
import de.tuclausthal.submissioninterface.persistence.datamodel.Participation;
import de.tuclausthal.submissioninterface.persistence.datamodel.User;
import de.tuclausthal.submissioninterface.util.Util;
import org.hibernate.Session;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class LectureService {

    private Session session;
    private LectureDAOIf lectureDAO;
    private UserDAOIf userDAO;
    private ParticipationDAOIf participationDAO ;

    private String message = "";

    public LectureService() {
        this.session = HibernateApiSessionManager.getSession();
        this.lectureDAO = DAOFactory.LectureDAOIf(session);
        this.userDAO = DAOFactory.UserDAOIf(session);
        this.participationDAO = DAOFactory.ParticipationDAOIf(session);
    }

    public Response getLecture(int lectureId, int userId) {

        Lecture lecture = lectureDAO.getLecture(lectureId);
        User user = userDAO.getUser(userId);
        Participation participation = participationDAO.getParticipation(user, lecture);

        if (lecture == null) {
            message = "Veranstaltung nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);

            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        if (participation == null) {
            if (lecture.getSemester() == Util.getCurrentSemester() && lecture.isAllowSelfSubscribe()) {
                message = "Sie versuchen auf eine Veranstaltung zuzugreifen, für die Sie nicht angemeldet sind";
                ErrorMessage errorMessage = new ErrorMessage(message);

                throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                        .entity(errorMessage).build());
            }

            message = "Sie versuchen auf eine Veranstaltung zuzugreifen, für die Sie nicht angemeldet sind. Eine Anmeldung ist nicht (mehr) möglich.";
            ErrorMessage errorMessage = new ErrorMessage(message);

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(errorMessage).build());
        }

        return Response
                .ok()
                .entity(lecture)
                .build();
    }
}