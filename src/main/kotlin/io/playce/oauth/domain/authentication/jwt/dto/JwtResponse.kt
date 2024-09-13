package io.playce.oauth.domain.authentication.jwt.dto


data class JwtResponse(
    val token: String,
    val refreshToken: String,
)