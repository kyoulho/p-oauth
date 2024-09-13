package io.playce.oauth.domain.authentication.jwt.token

import io.playce.oauth.domain.authentication.jwt.dto.SecurityUser
import io.playce.oauth.exception.ErrorCode
import io.playce.oauth.exception.PlayceOAuthException
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */

class JwtAuthenticationToken : AbstractAuthenticationToken {

    private var rawAccessToken: RawAccessJwtToken? = null
    private var securityUser: SecurityUser? = null

    constructor(unsafeToken: RawAccessJwtToken?) : super(null) {
        rawAccessToken = unsafeToken
        this.isAuthenticated = false
    }

    constructor(securityUser: SecurityUser, authorities: MutableSet<out GrantedAuthority>) : super(authorities) {
        eraseCredentials()
        this.securityUser = securityUser
        super.setAuthenticated(true)
    }

    override fun setAuthenticated(authenticated: Boolean) {
        if (authenticated) {
            throw object : AuthenticationException(ErrorCode.NOT_TRUSTED_TOKEN_SET.name) {
                private val serialVersionUID = 7002106587436364722L
            }
        }
        super.setAuthenticated(false)
    }

    override fun getCredentials(): Any? {
        return rawAccessToken
    }

    override fun getPrincipal(): Any? {
        return securityUser
    }

    override fun eraseCredentials() {
        super.eraseCredentials()
        rawAccessToken = null
    }

    companion object {
        private const val serialVersionUID = 7434255134956599275L
    }
}
