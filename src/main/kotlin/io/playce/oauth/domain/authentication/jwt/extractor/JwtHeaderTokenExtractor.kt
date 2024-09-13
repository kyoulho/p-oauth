package io.playce.oauth.domain.authentication.jwt.extractor

import io.playce.oauth.config.OAuthProperties
import io.playce.oauth.exception.ErrorCode
import io.playce.oauth.exception.PlayceOAuthException
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Component

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
class JwtHeaderTokenExtractor(
    private val oAuthProperties: OAuthProperties
) : TokenExtractor {
    override fun extract(payload: String): String {
        if (payload.isBlank()) {
            throw PlayceOAuthException(ErrorCode.EMPTY_AUTH_HEADER, HttpStatus.UNAUTHORIZED)
        }
        val bearerLength = oAuthProperties.bearerPrefix.length + 1
        if (payload.length < bearerLength) {
            throw PlayceOAuthException(ErrorCode.INVALID_AUTH_HEADER, HttpStatus.UNAUTHORIZED)
        }

        return payload.substring(bearerLength)
    }

}
