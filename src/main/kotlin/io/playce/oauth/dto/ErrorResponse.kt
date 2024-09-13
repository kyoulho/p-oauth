package io.playce.oauth.dto

data class ErrorResponse(
    val errorCode: String,
    val defaultMessage: String,
    val stackTrace: String,
)
