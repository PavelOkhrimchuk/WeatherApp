package service;

import exception.session.SessionCreationException;
import exception.session.SessionInvalidationException;
import exception.session.SessionNotFoundException;
import model.Session;
import model.User;
import repository.SessionRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session createSession(User user, int sessionDurationHours) {
        try {
            Session session = new Session();
            session.setId(UUID.randomUUID());
            session.setUser(user);
            session.setExpiresAt(LocalDateTime.now().plusHours(sessionDurationHours));
            return sessionRepository.save(session);
        } catch (Exception e) {
            throw new SessionCreationException("Failed to create session for user: " + user.getLogin(), e);
        }
    }

    public Optional<Session> findSessionById(UUID sessionId) {
        return sessionRepository.findById(sessionId);
    }


    public void invalidateSession(UUID sessionId) {
        try {
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new SessionNotFoundException("Session with ID " + sessionId + " not found."));
            sessionRepository.delete(session);
        } catch (Exception e) {
            throw new SessionInvalidationException("Failed to invalidate session with ID: " + sessionId, e);
        }
    }



    public Optional<Session> getValidSession(UUID sessionId) {
        return findSessionById(sessionId)
                .filter(session -> session.getExpiresAt().isAfter(LocalDateTime.now()));
    }
}
