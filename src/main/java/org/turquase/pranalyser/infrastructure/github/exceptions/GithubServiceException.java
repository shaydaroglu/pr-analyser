package org.turquase.pranalyser.infrastructure.github.exceptions;

public class GithubServiceException extends RuntimeException {
    public GithubServiceException(String message) {
        super(message);
    }

    public GithubServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
