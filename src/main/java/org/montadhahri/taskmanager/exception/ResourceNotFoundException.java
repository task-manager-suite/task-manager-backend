package org.montadhahri.taskmanager.exception;

/**
 * resource not found exception
 * @author mdh
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
