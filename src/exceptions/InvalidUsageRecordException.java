package exceptions;

public class InvalidUsageRecordException extends RuntimeException {
    public InvalidUsageRecordException(String message) {
        super(message);
    }
}
