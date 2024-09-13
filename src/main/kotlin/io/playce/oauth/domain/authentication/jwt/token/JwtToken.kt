package io.playce.oauth.domain.authentication.jwt.token

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */
interface JwtToken {
    fun getToken(): String?
}