package io.playce.oauth.domain.authentication.jwt.provider

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.playce.oauth.config.OAuthProperties
import io.playce.oauth.domain.authentication.dto.CustomUserDetails
import io.playce.oauth.domain.authentication.jwt.dto.SecurityUser
import io.playce.oauth.domain.authentication.jwt.token.JwtAuthenticationToken
import io.playce.oauth.domain.authentication.jwt.token.RawAccessJwtToken
import io.playce.oauth.exception.ErrorCode
import io.playce.oauth.exception.PlayceOAuthException
import io.playce.oauth.service.CustomUserDetailsService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.stream.Collectors

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */

@Component
class JwtAuthenticationProvider(
    private val oAuthProperties: OAuthProperties,
) : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val rawAccessToken: RawAccessJwtToken = authentication.credentials as RawAccessJwtToken
        val jwsClaims: Jws<Claims> = rawAccessToken.parseClaims(oAuthProperties.secretKey)
        val userMap = (jwsClaims.body["user"] ?: throw PlayceOAuthException(
            ErrorCode.INVALID_JWT_CLAIMS,
            HttpStatus.UNAUTHORIZED
        )) as LinkedHashMap<*,*>

        val roles = jwsClaims.body["roles"] as List<String>
        val authorities = roles.map { SimpleGrantedAuthority(it) }.toMutableSet()

        val securityUser = SecurityUser(
            userId = userMap["userId"].toString().toLong(),
            userLonginId = userMap["userLoginId"].toString(),
            username = userMap["username"].toString(),
            adminChangeYn = userMap["adminChangeYn"].toString(),
            authorities = authorities,
            password = null,
        )

        return JwtAuthenticationToken(securityUser, authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}