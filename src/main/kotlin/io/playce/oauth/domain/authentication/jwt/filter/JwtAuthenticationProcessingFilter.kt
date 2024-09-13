package io.playce.oauth.domain.authentication.jwt.filter

import io.playce.oauth.config.OAuthProperties
import io.playce.oauth.domain.authentication.jwt.extractor.TokenExtractor
import io.playce.oauth.domain.authentication.jwt.provider.JwtAuthenticationProvider
import io.playce.oauth.domain.authentication.jwt.token.JwtAuthenticationToken
import io.playce.oauth.domain.authentication.jwt.token.RawAccessJwtToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */

class JwtAuthenticationProcessingFilter(
    private val tokenExtractor: TokenExtractor,
    private val matcher: RequestMatcher,
    private val oAuthProperties: OAuthProperties,
    private val jwtAuthenticationProvider: JwtAuthenticationProvider
) :
    AbstractAuthenticationProcessingFilter(matcher) {


    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse?
    ): Authentication {
        val tokenPayload = httpServletRequest.getHeader(oAuthProperties.authenticationHeaderName)
        val token = RawAccessJwtToken(tokenExtractor.extract(tokenPayload))

        return jwtAuthenticationProvider.authenticate(JwtAuthenticationToken(token))
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain,
        authResult: Authentication?
    ) {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authResult
        SecurityContextHolder.setContext(context)
        chain.doFilter(request, response)
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        failed: AuthenticationException?
    ) {
        SecurityContextHolder.clearContext()
    }
}
