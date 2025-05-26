package com.example.demo.result;

public enum ResultCode {
    SUCCESS(200),

    FAIL(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    BAD_REQUEST(421),
    VALIDATION_FAILED(422),
    LOGIN_FAILED(423),
    REQUEST_TOO_MANY(429),

    INTERNAL_SERVER_ERROR(500),
    ERROR(503),
    GATEWAY_TIMEOUT(504);

    public int code;

    ResultCode(int code) {
        this.code = code;
    }
}