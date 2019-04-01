package be.somedi.printandsend.config;

import be.somedi.printandsend.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(CaregiverNotFoundException.class)
    public ResponseEntity<String> handleCaregiverNotFound(CaregiverNotFoundException cnfe) {
        return ResponseEntity.status(404).body(cnfe.getMessage());
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<String> handleCaregiverNotFound(PatientNotFoundException pnfe) {
        return ResponseEntity.status(404).body(pnfe.getMessage());
    }

    @ExceptionHandler(PathNotFoundException.class)
    public ResponseEntity<String> handlePathNotFound(PathNotFoundException pnfe) {
        return ResponseEntity.status(404).body(pnfe.getMessage());
    }

    @ExceptionHandler(PrinterNotFoundException.class)
    public ResponseEntity<String> handlePrinterNotFound(PrinterNotFoundException pnfe) {
        return ResponseEntity.status(404).body(pnfe.getMessage());
    }

    @ExceptionHandler(ReadException.class)
    public ResponseEntity<String> handleRead(ReadException re) {
        return ResponseEntity.status(404).body(re.getMessage());
    }
}
