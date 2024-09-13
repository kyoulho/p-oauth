package io.playce.oauth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.playce.oauth.domain.authentication.dto.ChangePasswordRequest
import io.playce.oauth.domain.authentication.jwt.dto.JwtResponse
import io.playce.oauth.domain.authentication.dto.LoginRequest
import io.playce.oauth.util.GeneralCipherUtil
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
internal class AuthControllerTest @Autowired constructor(
    val objectMapper: ObjectMapper,
    val mockMvc: MockMvc,
) {
    @Test
    @DisplayName("로그인에 성공한다.")
    fun loginSuccess() {
        val loginRequest = LoginRequest(
            GeneralCipherUtil.encrypt("admin"),
            GeneralCipherUtil.encrypt("admin")
        )
        val jwtTokenJson = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
            .andReturn().response.contentAsString

        assertDoesNotThrow {
            objectMapper.readValue(jwtTokenJson, JwtResponse::class.java)
        }
    }

    @Test
    @DisplayName("로그인에 실패한다.")
    fun loginFail() {
        val loginRequest = LoginRequest(
            GeneralCipherUtil.encrypt("admin"),
            GeneralCipherUtil.encrypt("as")
        )
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    @DisplayName("토큰 재발급에 성공한다")
    fun refreshToken() {
        val loginRequest = LoginRequest(
            GeneralCipherUtil.encrypt("admin"),
            GeneralCipherUtil.encrypt("admin")
        )
        val tokenJson = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
            .andReturn().response.contentAsString

        val jwtResponse = objectMapper.readValue(tokenJson, JwtResponse::class.java)

        val headers = HttpHeaders().also {
            it.setBearerAuth(jwtResponse.refreshToken)
        }

        val newTokenJson = mockMvc.perform(
            get("/api/auth/refresh-token")
                .headers(headers)
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        assertDoesNotThrow {
            objectMapper.readValue(newTokenJson, JwtResponse::class.java)
        }
    }

    @Test
    @DisplayName("비밀번호 변경에 성공한다.")
    @Transactional
    fun changePassword() {
        val loginRequest = LoginRequest(
            GeneralCipherUtil.encrypt("admin"),
            GeneralCipherUtil.encrypt("admin"),
        )

        val tokenJson = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
            .andReturn().response.contentAsString

        val jwtResponse = objectMapper.readValue(tokenJson, JwtResponse::class.java)

        val headers = HttpHeaders().also {
            it.setBearerAuth(jwtResponse.token)
        }

        val changePasswordRequest = ChangePasswordRequest(
            GeneralCipherUtil.encrypt("admin"),
            GeneralCipherUtil.encrypt("nimda")
        )

        mockMvc.perform(
            put("/api/auth/change-password")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(changePasswordRequest))
        )
            .andExpect(status().isOk)
    }
}