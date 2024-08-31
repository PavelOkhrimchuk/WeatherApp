import model.Location;
import model.Session;
import model.User;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class TestHibernateUtil {

//    private static final SessionFactory sessionFactory = buildSessionFactory();
//
//    private static SessionFactory buildSessionFactory() {
//        try {
//
//            Configuration configuration = new Configuration().configure("hibernate-test.cfg.xml");
//
//
//            configuration.addAnnotatedClass(User.class);
//            configuration.addAnnotatedClass(Location.class);
//            configuration.addAnnotatedClass(Session.class);
//
//
//            return configuration.buildSessionFactory();
//
//        } catch (HibernateException ex) {
//            System.err.println("Initial SessionFactory creation failed. " + ex);
//            throw new ExceptionInInitializerError(ex);
//        }
//    }
//
//    public static SessionFactory getSessionFactory() {
//        return sessionFactory;
//    }
//
//    public static void shutdown() {
//        getSessionFactory().close();
//    }
}
