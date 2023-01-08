package org.example.exceptions;

/**
 * Should be thrown if cyclic dependency was found
 */
public class CyclicDependencyException extends Exception {
    String message;

    public CyclicDependencyException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
