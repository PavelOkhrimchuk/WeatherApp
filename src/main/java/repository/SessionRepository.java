package repository;

import model.Session;
import model.User;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class SessionRepository {

    private final SessionFactory sessionFactory;

    public SessionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session save(Session session) {
        try (org.hibernate.Session session1 = sessionFactory.openSession()) {
            Transaction transaction = session1.beginTransaction();

            session1.persist(session);

            transaction.commit();
            return session;
        }
    }

    public List<Session> findAll() {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Session", Session.class).list();

        }
    }

    public List<Session> findByUser(User user) {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Session WHERE user =: user", Session.class)
                    .setParameter("user", user)
                    .list();
        }
    }


}
