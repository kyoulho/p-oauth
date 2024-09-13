package io.playce.oauth.domain.authentication.service

import io.playce.oauth.config.logger
import io.playce.oauth.domain.authentication.dto.CustomUserDetails
import io.playce.oauth.domain.authentication.jwt.dto.JwtResponse
import io.playce.oauth.domain.authentication.dto.LoginRequest
import io.playce.oauth.domain.authentication.dto.ChangePasswordRequest
import io.playce.oauth.domain.user.service.UserAccessService
import io.playce.oauth.exception.ErrorCode
import io.playce.oauth.exception.PlayceOAuthException
import io.playce.oauth.repository.UserAccessRepository
import io.playce.oauth.repository.findByUserLoginIdOrThrow
import io.playce.oauth.service.JwtManager
import io.playce.oauth.util.GeneralCipherUtil
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtManager: JwtManager,
    private val userAccessRepository: UserAccessRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userAccessService: UserAccessService
) {

    fun login(loginRequest: LoginRequest): JwtResponse {
        logger().info("Login Request incoming: [$loginRequest]")
        val decryptLoginId = GeneralCipherUtil.decrypt(loginRequest.username)
        val decryptPassword = GeneralCipherUtil.decrypt(loginRequest.password)

        val token = UsernamePasswordAuthenticationToken(decryptLoginId, decryptPassword)
        val userDetails = authenticationManager.authenticate(token).principal as CustomUserDetails

        // 삭제 여부 확인
        if (userDetails.isDeleted()) {
            throw PlayceOAuthException(ErrorCode.NOT_FOUND_USER, HttpStatus.BAD_REQUEST)
        }

        if (!passwordEncoder.matches(decryptPassword, userDetails.password)) {
            logger().info("Password Incorrect [$decryptLoginId]")
            userAccessService.updateLoginFailureCnt(decryptLoginId)
            throw PlayceOAuthException(ErrorCode.PASSWORD_INCORRECT, HttpStatus.BAD_REQUEST)
        }

        if (userDetails.lockYn) {
            logger().info("Account was locked [$decryptLoginId]")
            throw PlayceOAuthException(ErrorCode.ACCOUNT_LOCKED, HttpStatus.BAD_REQUEST)
        }

        userAccessService.resetLoginFailureCnt(decryptLoginId)
        logger().info("Authentication Success [$decryptLoginId]")

        return jwtManager.createJWT(userDetails)
    }

    fun reissue(refreshToken: String): JwtResponse {
        val userDetails = jwtManager.getAuthentication(refreshToken).principal as CustomUserDetails

        return jwtManager.createJWT(userDetails)
    }

    fun changePassword(accessToken: String, changePasswordRequest: ChangePasswordRequest) {
        val userDetails = jwtManager.getAuthentication(accessToken).principal as CustomUserDetails
        val userAccess = userAccessRepository.findByUserLoginIdOrThrow(userDetails.userLonginId)

        val currentPassword = GeneralCipherUtil.decrypt(changePasswordRequest.currentPassword)
        val newPassword = GeneralCipherUtil.decrypt(changePasswordRequest.newPassword)

        validatePassword(userAccess.password, newPassword, currentPassword)

        userAccess.password = passwordEncoder.encode(newPassword)
        userAccess.adminChangeYn = "N"

        logger().info("Password Changed Username: ${userAccess.username}")
    }

    private fun validatePassword(dbPassword: String, newPassword: String, currentPassword: String) {
        if (!passwordEncoder.matches(currentPassword, dbPassword)) {
            throw PlayceOAuthException(ErrorCode.INVALID_CURRENT_PASSWORD, HttpStatus.BAD_REQUEST)
        }
        if (passwordEncoder.matches(newPassword, dbPassword)) {
            throw PlayceOAuthException(ErrorCode.INVALID_NEW_PASSWORD, HttpStatus.BAD_REQUEST)
        }
    }
}