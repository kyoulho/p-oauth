package io.playce.oauth.domain.authentication.dto

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
)

