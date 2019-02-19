package be.somedi.printandsend.exceptions;

public class PrinterNotFoundException extends RuntimeException {

    public PrinterNotFoundException() {
    }

    public PrinterNotFoundException(String message) {
        super(message);
    }

    public PrinterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
