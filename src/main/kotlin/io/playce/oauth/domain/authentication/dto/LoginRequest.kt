package io.playce.oauth.domain.authentication.dto

data class LoginRequest(
    val username: String,
    val password: String,
)
