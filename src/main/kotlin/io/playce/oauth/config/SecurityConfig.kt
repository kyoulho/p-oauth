package io.playce.oauth.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.playce.oauth.domain.authentication.jwt.extractor.TokenExtractor
import io.playce.oauth.domain.authentication.jwt.filter.ExceptionHandlerFilter
import io.playce.oauth.domain.authentication.jwt.filter.JwtAuthenticationProcessingFilter
import io.playce.oauth.domain.authentication.jwt.filter.SkipPathRequestMatcher
import io.playce.oauth.domain.authentication.jwt.provider.JwtAuthenticationProvider
import io.playce.oauth.service.IdPasswordAuthenticationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*

@EnableWebSecurity
class SecurityConfig (
    private val tokenExtractor: TokenExtractor,
    private val oAuthProperties: OAuthProperties,
    private val jwtAuthenticationProvider: JwtAuthenticationProvider,
    private val idPasswordAuthenticationProvider: IdPasswordAuthenticationProvider,
    private val objectMapper: ObjectMapper,
    private val messageSource: MessageSource,

){
    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager? {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().also {
            it.addAllowedOriginPattern("*")
            it.addAllowedHeader("*")
            it.addAllowedMethod("*")
            it.allowedOrigins = listOf("*")
            it.addExposedHeader("Content-Disposition")
        }

        return UrlBasedCorsConfigurationSource().also {
            it.registerCorsConfiguration("/**", configuration)
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf().disable()
            .headers().frameOptions().disable()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .httpBasic()
            .disable()

            .authorizeRequests()
            .antMatchers("/**").permitAll()

            .and()
            .addFilterBefore(exceptionHandlerFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .formLogin().disable()

            .cors()
            .configurationSource(corsConfigurationSource())
            .and()

            .build()
    }

    @Throws(Exception::class)
    private fun jwtAuthenticationProcessingFilter(): JwtAuthenticationProcessingFilter {
        val filter = JwtAuthenticationProcessingFilter(tokenExtractor, SkipPathRequestMatcher(permitAllEndpointList, API_ROOT_URL), oAuthProperties, jwtAuthenticationProvider)
        filter.setAuthenticationManager(authenticationManager(AuthenticationConfiguration()))
        return filter
    }

    private fun exceptionHandlerFilter(): ExceptionHandlerFilter {
        return ExceptionHandlerFilter(messageSource, objectMapper)
    }

    @Autowired
    fun registerProvider(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(idPasswordAuthenticationProvider)
    }


    companion object {
        private val CONSOLE_URL = "/console/**"
        private val API_AUTH_URL = "/api/auth/**"
        private val API_ROOT_URL = "/api/**"
        private val permitAllEndpointList = listOf(
            CONSOLE_URL,
            API_AUTH_URL,
            "/api/common/codes"
        )
    }


}