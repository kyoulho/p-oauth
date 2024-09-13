package io.playce.oauth.domain.authentication.jwt.dto

data class JwtPayload(
    val userId: Long?,
    val userLoginId: String,
    val username: String,
    val adminChangeYn: String,
)