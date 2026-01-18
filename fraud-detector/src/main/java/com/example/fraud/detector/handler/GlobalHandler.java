package com.example.fraud.detector.handler;

import com.example.fraud.detector.exception.GlobalException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(GlobalException ex) {

        HttpStatus status;

        switch (ex.getType()) {
            case INSUFFICIENT_BALANCE:
                status = HttpStatus.BAD_REQUEST; // 400
                break;
            case UNAUTHORIZED:
                status = HttpStatus.UNAUTHORIZED; // 401
                break;
            case NOT_FOUND:
                status = HttpStatus.NOT_FOUND; // 404
                break;
            default:
                status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        }

        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());

        // Kuncinya: return ResponseEntity, jangan throw
        return new ResponseEntity<>(body, status);
    }
}
