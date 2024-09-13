package io.playce.oauth.exception

import org.springframework.http.HttpStatus

class PlayceOAuthException : RuntimeException {
    val errorCode: ErrorCode
    val httpStatus: HttpStatus
    val arguments: Array<out Any>?

    constructor(errorCode: ErrorCode, httpStatus: HttpStatus) : super() {
        this.errorCode = errorCode
        this.httpStatus = httpStatus
        this.arguments = null
    }

    constructor(errorCode: ErrorCode, httpStatus: HttpStatus, cause: Throwable) : super(cause) {
        this.errorCode = errorCode
        this.httpStatus = httpStatus
        this.arguments = null
    }

    constructor(errorCode: ErrorCode, httpStatus: HttpStatus, vararg arguments: Any) : super() {
        this.errorCode = errorCode
        this.httpStatus = httpStatus
        this.arguments = arguments
    }
}
