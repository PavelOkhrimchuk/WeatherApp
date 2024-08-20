package service;

import model.Session;
import model.User;
import repository.SessionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session createSession(User user, int sessionDurationHours) {
        Session session = new Session();
        session.setId(UUID.randomUUID());
        session.setUser(user);
        session.setExpiresAt(LocalDateTime.now().plusHours(sessionDurationHours));
        return sessionRepository.save(session);
    }

    public Optional<Session> findSessionById(UUID sessionId) {
        return sessionRepository.findById(sessionId);
    }

    public List<Session> getActiveSessionsForUser(User user) {
        return sessionRepository.findByUser(user);
    }

    public void invalidateSession(UUID sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    public void invalidateExpiredSessions() {
        sessionRepository.deleteExpiredSession();
    }

    public Optional<Session> getValidSession(UUID sessionId) {
        return findSessionById(sessionId)
                .filter(session -> session.getExpiresAt().isAfter(LocalDateTime.now()));
    }
}
