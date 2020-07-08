package com.cs446.group7.bruno.location;

public class LocationServiceException extends Exception {
    enum ErrorType {
        NULL_LOCATION_ERROR,
        OTHER_ERROR
    }

    private ErrorType errorType;

    public LocationServiceException(final ErrorType errorType) {
        this.errorType = errorType;
    }

    public LocationServiceException(final Exception underlyingException) {
        super(underlyingException);
        this.errorType = ErrorType.OTHER_ERROR;
    }
}
