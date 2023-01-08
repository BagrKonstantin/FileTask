package org.example.exceptions;

/**
 * Should be thrown if cyclic dependency was found
 */
public class CyclicDependencyException extends Exception {
    public CyclicDependencyException(String message) {
        super(message);
    }
}
