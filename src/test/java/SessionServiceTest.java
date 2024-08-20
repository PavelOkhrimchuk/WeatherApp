import model.Session;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.SessionRepository;
import service.SessionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    private User user;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setLogin("testUser");
        user.setPassword("testPassword");
    }


    @Test
    public void testCreateSession() {
        Session session = new Session();
        session.setId(UUID.randomUUID());
        session.setUser(user);
        session.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session createdSession = sessionService.createSession(user, 1);

        assertNotNull(createdSession, "Session should be created");
        assertEquals(user.getId(), createdSession.getUser().getId(), "Session user ID should match the registered user ID");
        assertTrue(createdSession.getExpiresAt().isAfter(LocalDateTime.now()), "Session expiration time should be set correctly");
    }

    @Test
    public void testGetValidSession_whenSessionIsValid() {

        Session session = new Session();
        session.setId(UUID.randomUUID());
        session.setUser(user);
        session.setExpiresAt(LocalDateTime.now().plusHours(1)); // Не истекла

        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));

        Optional<Session> validSession = sessionService.getValidSession(session.getId());

        assertTrue(validSession.isPresent(), "Session should be valid");
        assertEquals(user.getId(), validSession.get().getUser().getId(), "Session user ID should match the registered user ID");
    }

    @Test
    public void testGetValidSession_whenSessionIsExpired() {
        Session session = new Session();
        session.setId(UUID.randomUUID());
        session.setUser(user);
        session.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));

        Optional<Session> validSession = sessionService.getValidSession(session.getId());

        assertFalse(validSession.isPresent(), "Session should be expired");
    }




}
