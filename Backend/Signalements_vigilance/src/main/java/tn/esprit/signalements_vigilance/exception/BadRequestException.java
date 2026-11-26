package tn.esprit.signalements_vigilance.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
