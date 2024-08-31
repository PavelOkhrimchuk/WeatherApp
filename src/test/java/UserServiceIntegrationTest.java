import model.Session;
import model.User;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.SessionRepository;
import repository.UserRepository;
import service.SessionService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceIntegrationTest {

//    private SessionFactory sessionFactory;
//    private UserRepository userRepository;
//    private SessionRepository sessionRepository;
//    private SessionService sessionService;
//
//
//    @BeforeEach
//    public void setUp() {
//        sessionFactory = TestHibernateUtil.getSessionFactory();
//        userRepository = new UserRepository(sessionFactory);
//        sessionRepository = new SessionRepository(sessionFactory);
//        sessionService = new SessionService(sessionRepository);
//    }
//
//    @AfterEach
//    public void tearDown() {
//        TestHibernateUtil.shutdown();
//    }
//
//
//    @Test
//    public void testUserAuthorizationCreatesSession() {
//
//        User user = new User();
//        user.setLogin("testUser");
//        user.setPassword("hashedPassword");
//        userRepository.save(user);
//
//
//        Session session = sessionService.createSession(user, 2);
//
//
//        assertNotNull(session, "Session should be created");
//        assertEquals(user.getId(), session.getUser().getId(), "Session user ID should match the registered user ID");
//
//
//        Optional<Session> retrievedSession = sessionRepository.findById(session.getId());
//        assertTrue(retrievedSession.isPresent(), "Session should be retrievable from the database");
//        assertEquals(user.getId(), retrievedSession.get().getUser().getId(), "Retrieved session user ID should match the registered user ID");
//    }
}
