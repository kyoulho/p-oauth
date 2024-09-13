package io.playce.oauth.domain.authentication.controller

import io.playce.oauth.domain.authentication.dto.ChangePasswordRequest
import io.playce.oauth.domain.authentication.jwt.dto.JwtResponse
import io.playce.oauth.domain.authentication.dto.LoginRequest
import io.playce.oauth.domain.authentication.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): JwtResponse {
        return authService.login(loginRequest)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/refresh-token")
    fun refreshToken(@RequestHeader(value = "Authorization") refreshToken: String): JwtResponse {
        return authService.reissue(refreshToken)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/change-password")
    fun changePassword(@RequestHeader(value = "Authorization") accessToken: String, @RequestBody changePasswordRequest: ChangePasswordRequest) {
        return authService.changePassword(accessToken,changePasswordRequest)
    }
}