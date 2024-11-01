package com.github.traunin.triangulation;

/**
 * Thrown to indicate that the triangulation cannot be performed.
 */
public class TriangulationException extends RuntimeException {
    /**
     * Constructs an TriangulationException with the specified
     * detail message.
     *
     * @param errorMessage the detail message
     */
    public TriangulationException(String errorMessage) {
        super(errorMessage);
    }
}
