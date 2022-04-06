package de.tuclausthal.submissioninterface.api.util;

import de.tuclausthal.submissioninterface.util.HibernateSessionHelper;
import org.hibernate.Session;

public class HibernateApiSessionManager {

    static private Session session = null;

    /**
     * Singleton to make sure only one instance of Hibernate session is created
     *
     * @return the hibernate session
     */
    public static Session getSession() {
        if (session == null) {
            session = HibernateSessionHelper.getSessionFactory().openSession();
        }
        return session;
    }
}