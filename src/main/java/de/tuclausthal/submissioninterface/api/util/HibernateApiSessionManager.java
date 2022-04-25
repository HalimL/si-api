package de.tuclausthal.submissioninterface.api.util;

import de.tuclausthal.submissioninterface.util.HibernateSessionHelper;
import org.hibernate.Session;

public class HibernateApiSessionManager {

    private static Session session;

    public static Session getSession() {
        session = HibernateSessionHelper.getSessionFactory().openSession();
        return session;
    }

    public static void closeSession() {
        HibernateSessionHelper.getSessionFactory().getCurrentSession().close();
    }
}