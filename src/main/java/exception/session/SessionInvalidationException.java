package exception.session;

public class SessionInvalidationException extends RuntimeException{

    public SessionInvalidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
