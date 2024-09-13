package io.playce.oauth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "oauth-server")
data class OAuthProperties(
    val issuer: String,
    val subject: String,
    val secretKey: String,
    val accessTokenExpireTime: Long,
    val refreshTokenExpireTime: Long,
    val bearerPrefix: String,
    val authenticationHeaderName: String,
)