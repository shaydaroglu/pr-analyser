package org.turquase.pranalyser.domain.exceptions;

public class PrStatisticCalculatorServiceException extends RuntimeException {
    public PrStatisticCalculatorServiceException(String message) {
        super(message);
    }

    public PrStatisticCalculatorServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
