package io.playce.oauth.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.playce.oauth.config.OAuthProperties
import io.playce.oauth.domain.authentication.dto.CustomUserDetails
import io.playce.oauth.domain.authentication.jwt.dto.JwtPayload
import io.playce.oauth.domain.authentication.jwt.dto.JwtResponse
import io.playce.oauth.exception.ErrorCode
import io.playce.oauth.exception.PlayceOAuthException
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class JwtManager(
    private val oAuthProperties: OAuthProperties,
    private val customUserDetailsService: CustomUserDetailsService
) {
    fun getAuthentication(accessToken: String): Authentication {
        val claims = parseClaims(accessToken)
        val jwtPayload = (claims["user"] ?: throw PlayceOAuthException(
            ErrorCode.INVALID_JWT_CLAIMS,
            HttpStatus.BAD_REQUEST
        )) as LinkedHashMap<*, *>

        val userDetails = customUserDetailsService.loadUserByUsername(jwtPayload["userLoginId"].toString())
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    private fun parseClaims(bearerToken: String): Claims {
        if (!bearerToken.startsWith(oAuthProperties.bearerPrefix)) {
            throw PlayceOAuthException(ErrorCode.NOT_BEARER_TOKEN, HttpStatus.BAD_REQUEST)
        }

        val token = bearerToken.substring(7)

        return try {
            Jwts.parser().setSigningKey(oAuthProperties.secretKey).parseClaimsJws(token).body
        } catch (e: ExpiredJwtException) {
            throw PlayceOAuthException(ErrorCode.EXPIRED_JWT_TOKEN, HttpStatus.UNAUTHORIZED)
        } catch (e: Exception) {
            throw PlayceOAuthException(ErrorCode.BAD_SIGNING_KEY, HttpStatus.BAD_REQUEST, e)
        }
    }

    private fun generateToken(claims: Claims, expireTime: Long): String {
        val now = Instant.now()
        return Jwts.builder()
            .setClaims(claims)
            .setIssuer(oAuthProperties.issuer)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(expireTime)))
            .signWith(SignatureAlgorithm.HS512, oAuthProperties.secretKey)
            .compact()
    }

    private fun createAccessToken(userDetails: CustomUserDetails): String {
        val claims = Jwts.claims().setSubject(oAuthProperties.subject)
        claims["user"] = JwtPayload(userDetails.userId, userDetails.userLonginId, userDetails.username, userDetails.adminChangeYn)
        claims["roles"] = userDetails.authorities.map { it.authority }.toList()
        val expireTime = oAuthProperties.accessTokenExpireTime * 60
        return generateToken(claims, expireTime)
    }

    private fun createRefreshToken(userDetails: CustomUserDetails): String {
        val claims = Jwts.claims().setSubject(oAuthProperties.subject)
        claims["user"] = JwtPayload(userDetails.userId, userDetails.userLonginId, userDetails.username, userDetails.adminChangeYn)
        claims["roles"] = userDetails.authorities.map { it.authority }.toList()
        val expireTime = oAuthProperties.refreshTokenExpireTime * 60
        return generateToken(claims, expireTime)
    }

    fun createJWT(userDetails: CustomUserDetails): JwtResponse {
        return JwtResponse(createAccessToken(userDetails), createRefreshToken(userDetails))
    }
}