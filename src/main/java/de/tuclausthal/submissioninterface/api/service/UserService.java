package de.tuclausthal.submissioninterface.api.service;

import de.tuclausthal.submissioninterface.api.error.ErrorMessage;
import de.tuclausthal.submissioninterface.api.util.HibernateApiSessionManager;
import de.tuclausthal.submissioninterface.persistence.dao.DAOFactory;
import de.tuclausthal.submissioninterface.persistence.dao.UserDAOIf;
import de.tuclausthal.submissioninterface.persistence.datamodel.User;
import org.hibernate.Session;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


public class UserService {

    private Session session;
    private UserDAOIf userDAO;

    private String message = "";

    public UserService() {
        this.session = HibernateApiSessionManager.getSession();
        this.userDAO = DAOFactory.UserDAOIf(session);
    }

    public Response getUserById(int userId) {

        User user = userDAO.getUser(userId);

        if (user == null) {
            message = "User not found";
            ErrorMessage errorMessage = new ErrorMessage(message);

            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        return Response
                .ok()
                .entity(user)
                .build();
    }

    public Response getUserByUsername(String username) {

        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            message = "User not found";
            ErrorMessage errorMessage = new ErrorMessage(message);

            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        return Response
                .ok()
                .entity(user)
                .build();
    }

    public Response getUserByEmail(String email) {

        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            message = "User not found";
            ErrorMessage errorMessage = new ErrorMessage(message);

            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        return Response
                .ok()
                .entity(user)
                .build();
    }
}
