package io.playce.oauth.exception

enum class ErrorCode {
    EXPIRED_JWT_TOKEN,
    NO_EXIST_PUB,
    NO_EXIST_PRIV,
    NOT_FOUND_USER,
    INVALID_REQUEST_PARAM,
    INVALID_CURRENT_PASSWORD,
    INVALID_NEW_PASSWORD,
    NOT_BEARER_TOKEN,
    BAD_SIGNING_KEY,
    INVALID_JWT_CLAIMS,
    UNSUPPORTED_JWT,
    JWT_EMPTY_CLAIMS,
    INVALID_JWT_FORM,
    NOT_TRUSTED_TOKEN_SET,
    EMPTY_AUTH_HEADER,
    INVALID_AUTH_HEADER,
    INIT_DB_FAILURE,
    PASSWORD_INCORRECT,
    ACCOUNT_LOCKED,
    DUPLICATED_USER_LOGIN_ID,
    DUPLICATED_EMAIL,
    NOT_FOUND_USER_ROLE,
    FORBIDDEN,
    UNAUTHORIZED,
}