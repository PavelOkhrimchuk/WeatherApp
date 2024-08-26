package repository;

import exception.UserAlreadyExistsException;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    private final SessionFactory sessionFactory;

    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User save(User user) {
        try (Session session = sessionFactory.openSession()) {
            Optional<User> existingUser = findByLogin(user.getLogin());
            if (existingUser.isPresent()) {
                throw new UserAlreadyExistsException("User with login " + user.getLogin() + " already exists.");
            }

            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user;
        }
    }

    public Optional<User> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);

        }
    }

    public void deleteById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
            }

            transaction.commit();
        }
    }

    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User ", User.class).list();
        }
    }

    public Optional<User> findByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResultOptional();
        }
    }

}
