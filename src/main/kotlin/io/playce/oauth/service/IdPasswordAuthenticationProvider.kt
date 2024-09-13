package io.playce.oauth.service

import io.playce.oauth.config.logger
import io.playce.oauth.domain.authentication.dto.CustomUserDetails
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class IdPasswordAuthenticationProvider(
    private val customUserDetailsService: CustomUserDetailsService,
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val userLoginId = authentication.name
        logger().info("Incoming Authentication Request [$userLoginId]")
        val userDetails = customUserDetailsService.loadUserByUsername(userLoginId) as CustomUserDetails
        return UsernamePasswordAuthenticationToken(userDetails, userDetails.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}