package exception.session;

public class SessionCreationException extends RuntimeException {

    public SessionCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
