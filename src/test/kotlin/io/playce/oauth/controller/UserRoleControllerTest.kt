package io.playce.oauth.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.playce.oauth.domain.authentication.dto.LoginRequest
import io.playce.oauth.domain.authentication.jwt.dto.JwtResponse
import io.playce.oauth.domain.user.dto.RoleResponse
import io.playce.oauth.domain.user.dto.RoleUserResponse
import io.playce.oauth.dto.*
import io.playce.oauth.util.GeneralCipherUtil
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRoleControllerTest @Autowired constructor(
    val objectMapper: ObjectMapper,
    val mockMvc: MockMvc,
) {
    var headers: HttpHeaders = HttpHeaders()

    @BeforeAll
    fun init() {
        val loginRequest = LoginRequest(
            GeneralCipherUtil.encrypt("admin"),
            GeneralCipherUtil.encrypt("admin"),
        )

        val tokenJson = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
            .andReturn().response.contentAsString

        val jwtResponse = objectMapper.readValue(tokenJson, JwtResponse::class.java)

        this.headers = HttpHeaders().also {
            it.setBearerAuth(jwtResponse.token)
        }
    }

    @Test
    @DisplayName("권한 목록 조회에 성공한다.")
    fun getUserRoles_test() {

        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/user-roles")
                .headers(headers)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        assertDoesNotThrow {
            val responseListType = object : TypeReference<List<RoleResponse>>() {}
            objectMapper.readValue(response, responseListType)
        }
    }

    @Test
    @DisplayName("권한 단건 조회에 성공한다.")
    fun getUserRole_test() {

        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/user-roles/1")
                .headers(headers)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        assertDoesNotThrow {
            objectMapper.readValue(response, RoleResponse::class.java)
        }
    }

    @Test
    @DisplayName("권한별 사용자 목록 조회에 성공한다.")
    fun getUsersByRoles_test() {

        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/user-roles/1/users")
                .headers(headers)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        assertDoesNotThrow {
            val responseListType = object : TypeReference<List<RoleUserResponse>>() {}
            objectMapper.readValue(response, responseListType)
        }
    }
}