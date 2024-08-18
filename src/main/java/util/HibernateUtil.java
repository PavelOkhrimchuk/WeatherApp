package util;

import model.Location;
import model.Session;
import model.User;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {

            Configuration configuration = new Configuration().configure();

            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Location.class);
            configuration.addAnnotatedClass(Session.class);

            return configuration.buildSessionFactory();

        } catch (HibernateException ex) {
            System.out.println("Initial SessionFactory creation failed. " + ex );
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    public static void shutdown() {
        getSessionFactory().close();
   }

}
