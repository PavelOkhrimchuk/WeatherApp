package repository;

import model.Session;
import model.User;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Optional<Session> findById(UUID id) {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            Session sessionEntity = session.get(Session.class, id);
            return Optional.ofNullable(sessionEntity);
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

    public void deleteById(UUID id) {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Session sessionToDelete = session.get(Session.class, id);
            if (sessionToDelete != null) {
                session.remove(sessionToDelete);
            }

            transaction.commit();
        }

    }

    public void delete(Session sessionEntity) {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.remove(sessionEntity);

            transaction.commit();
        }
    }

    public void deleteExpiredSession() {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.createQuery("DELETE FROM Session WHERE expiresAt < :now")
                    .setParameter("now", LocalDateTime.now())
                    .executeUpdate();

            transaction.commit();
        }

    }


}
