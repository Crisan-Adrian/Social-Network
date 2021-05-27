package exceptions;

/**
 * Exception class used by business layer
 */
public class ServiceException extends RuntimeException{

    public ServiceException(String message)
    {
        super(message);
    }
}
