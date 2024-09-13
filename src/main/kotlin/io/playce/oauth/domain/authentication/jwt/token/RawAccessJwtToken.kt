package io.playce.oauth.domain.authentication.jwt.token

import io.jsonwebtoken.*
import io.playce.oauth.exception.ErrorCode
import io.playce.oauth.exception.PlayceOAuthException
import org.springframework.http.HttpStatus

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */

class RawAccessJwtToken(
    private val token: String
) : JwtToken {
    override fun getToken(): String {
        return token
    }
    fun parseClaims(signingKey: String?): Jws<Claims> {
        return try {
            Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token)
        } catch (ex: SignatureException) {
            throw PlayceOAuthException(ErrorCode.BAD_SIGNING_KEY, HttpStatus.UNAUTHORIZED, ex)
        } catch (ex: MalformedJwtException) {
            throw PlayceOAuthException(ErrorCode.INVALID_JWT_FORM, HttpStatus.UNAUTHORIZED, ex)
        } catch (ex: ExpiredJwtException) {
            throw PlayceOAuthException(ErrorCode.EXPIRED_JWT_TOKEN, HttpStatus.UNAUTHORIZED, ex)
        } catch (ex: UnsupportedJwtException) {
            throw PlayceOAuthException(ErrorCode.UNSUPPORTED_JWT, HttpStatus.UNAUTHORIZED, ex)
        } catch (ex: IllegalArgumentException) {
            throw PlayceOAuthException(ErrorCode.JWT_EMPTY_CLAIMS, HttpStatus.UNAUTHORIZED, ex)
        }
    }
}
