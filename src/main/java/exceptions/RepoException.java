package exceptions;

/**
 * Exception class used by data access layer
 */
public class RepoException extends RuntimeException {

    public RepoException (String message) {super(message);}
}
