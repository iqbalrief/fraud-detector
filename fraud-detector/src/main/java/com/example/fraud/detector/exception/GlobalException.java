package com.example.fraud.detector.exception;


    public class GlobalException extends RuntimeException {
        private final ErrorType type;

        public enum ErrorType {
            INSUFFICIENT_BALANCE,
            UNAUTHORIZED,

            NOT_FOUND,
            OTHER
        }

        public GlobalException(String message, ErrorType type) {
            super(message);
            this.type = type;
        }

        public ErrorType getType() {
            return type;
        }
}
