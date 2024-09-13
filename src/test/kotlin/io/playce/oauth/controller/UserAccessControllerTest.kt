package io.playce.oauth.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.playce.oauth.domain.authentication.dto.LoginRequest
import io.playce.oauth.domain.authentication.jwt.dto.JwtResponse
import io.playce.oauth.domain.user.dto.UserAccessRequest
import io.playce.oauth.domain.user.dto.UserAccessResponse
import io.playce.oauth.domain.user.dto.UserRoleResponse
import io.playce.oauth.dto.*
import io.playce.oauth.util.GeneralCipherUtil
import org.junit.jupiter.api.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

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
internal class UserAccessControllerTest @Autowired constructor(
    val objectMapper: ObjectMapper,
    val mockMvc: MockMvc,
) {
    var headers: HttpHeaders = HttpHeaders()
    val logger: Logger? = LoggerFactory.getLogger(UserAccessControllerTest::class.java)

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
        logger?.info("======token {}", jwtResponse.token)
        this.headers = HttpHeaders().also {
            it.setBearerAuth(jwtResponse.token)
        }
    }

    @Test
    @DisplayName("사용자 목록 조회에 성공한다.")
    fun getUsers_test() {

        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/users")
                .headers(headers)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        assertDoesNotThrow {
            val responseListType = object : TypeReference<List<UserAccessResponse>>() {}
            objectMapper.readValue(response, responseListType)
        }
    }

    @Test
    @DisplayName("사용자 단건 조회에 성공한다.")
    fun getUser_test() {

        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/users/1")
                .headers(headers)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        assertDoesNotThrow {
            objectMapper.readValue(response, UserAccessResponse::class.java)
        }
    }

    @Test
    @DisplayName("사용자 등록에 성공한다.")
    @Transactional
    fun createUser_test() {

        val request = UserAccessRequest(
            "admin88",
            "oJCRGU021Lz8OwssL9iRO/lyeDnPTHD1FJcMghVtBn/Wo8pFuCafAgG8gRxxwiShEJZ7ktd31GXwnHPMT6lesHdeRJ5VatD4MRXgecheIp0veJ9YsiKJaBH47fMBlHw5yaA63s9lfccfOThJtgKk26pq0Edw+0EPrYoVmtU+wbY8u0Z+JN0w+PV/KuDjROZcx2ggzS9ZfpoN1rKZIGLsuoKMD5dg/dbsYN68603rp0TjLFmCz30OXmG4SM0PdHfSNO5SIFEoWtQfXiElq58HdoZkvipa3XCzEMvgN1e/0o2Y40eQpyTxo5C8ukBF+p+4Lh0a9h2oFYCapXHe0amrqw==",
            "홍길동88",
            "roro888@osci.kr",
            "CTO",
            "회사",
            2
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @DisplayName("사용자 수정에 성공한다.")
    @Transactional
    fun modifyUser_test() {

        val request = UserAccessRequest(
            "admin88",
            "oJCRGU021Lz8OwssL9iRO/lyeDnPTHD1FJcMghVtBn/Wo8pFuCafAgG8gRxxwiShEJZ7ktd31GXwnHPMT6lesHdeRJ5VatD4MRXgecheIp0veJ9YsiKJaBH47fMBlHw5yaA63s9lfccfOThJtgKk26pq0Edw+0EPrYoVmtU+wbY8u0Z+JN0w+PV/KuDjROZcx2ggzS9ZfpoN1rKZIGLsuoKMD5dg/dbsYN68603rp0TjLFmCz30OXmG4SM0PdHfSNO5SIFEoWtQfXiElq58HdoZkvipa3XCzEMvgN1e/0o2Y40eQpyTxo5C8ukBF+p+4Lh0a9h2oFYCapXHe0amrqw==",
            "홍길동88",
            "roro888@osci.kr",
            "CTO",
            "회사",
            2
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/users/1")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @DisplayName("사용자 삭제에 성공한다.")
    @Transactional
    fun deleteUser_test() {

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/users/1")
                .headers(headers)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @DisplayName("사용자별 권한 목록 조회에 성공한다.")
    fun getUserRoles_test() {

        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/users/1/user-roles")
                .headers(headers)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        assertDoesNotThrow {
            val responseListType = object : TypeReference<List<UserRoleResponse>>() {}
            objectMapper.readValue(response, responseListType)
        }
    }

    //@Test
    @DisplayName("사용자 데이터 등록")
    @Transactional
    fun createUsers_test() {

        for (i in 2..100) {
            val even: Boolean = i % 2 == 0
            var jobTitle = "Manager"
            var orgainzation = "Open Source Consulting Inc."
            var roleId = 1L
            if(!even) {
                jobTitle = "Researcher"
                orgainzation = "Playce Dev"
                roleId = 2L
            }

            val request = UserAccessRequest(
                "user${i}",
                "user${i}",
                "홍길동${i}",
                "roro${i}@osci.kr",
                jobTitle,
                orgainzation,
                roleId,
            )

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users")
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
        }
    }

    @Test
    @DisplayName("암호화 테스트")
    fun encrypt_test() {
        val str = "admin121"
        val encryptStr = GeneralCipherUtil.encrypt(str)
        logger?.info("${str} {}", encryptStr)
        logger?.info("${encryptStr} {}", GeneralCipherUtil.decrypt(encryptStr))
    }



}