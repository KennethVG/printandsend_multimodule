package be.somedi.printandsend.exceptions;

public class CaregiverNotFoundException extends RuntimeException {

    public CaregiverNotFoundException() {
    }

    public CaregiverNotFoundException(String message) {
        super(message);
    }

    public CaregiverNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
