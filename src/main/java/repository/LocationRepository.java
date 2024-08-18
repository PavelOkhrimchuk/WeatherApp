package repository;

import model.Location;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class LocationRepository {

    private final SessionFactory sessionFactory;

    public LocationRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Location save(Location location) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.persist(location);

            transaction.commit();
            return location;
        }
    }

    public Optional<Location> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Location location = session.get(Location.class, id);
            return Optional.ofNullable(location);

        }
    }

    public void deleteById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Location location = session.get(Location.class, id);
            if (location != null) {
                session.remove(location);
            }

            transaction.commit();
        }
    }

    public List<Location> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Location ", Location.class).list();

        }
    }

    public List<Location> findByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Location  WHERE user =:user", Location.class)
                    .setParameter("user", user)
                    .list();
        }

    }


}
