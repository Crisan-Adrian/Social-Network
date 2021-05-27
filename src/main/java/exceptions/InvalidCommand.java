package exceptions;

/**
 * Exception class used for console UI
 */
public class InvalidCommand extends RuntimeException {

    public InvalidCommand(String message) {
        super(message);
    }

}
