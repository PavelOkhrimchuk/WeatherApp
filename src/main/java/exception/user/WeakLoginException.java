package exception.user;

public class WeakLoginException extends RuntimeException{

    public WeakLoginException(String message) {
        super(message);
    }
}
