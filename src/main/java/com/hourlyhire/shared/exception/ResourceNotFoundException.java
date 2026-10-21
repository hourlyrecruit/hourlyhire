package com.hourlyhire.shared.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String id) {
        super(resourceName + " not found with id: " + id);
    }
}
