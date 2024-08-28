package exception.location;

public class InvalidCityNameException extends RuntimeException{
    public InvalidCityNameException(String message) {
        super(message);
    }
}
