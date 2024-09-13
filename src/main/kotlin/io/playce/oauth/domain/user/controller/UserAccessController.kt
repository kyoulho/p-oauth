package io.playce.oauth.domain.user.controller

import io.playce.oauth.domain.user.dto.UserAccessRequest
import io.playce.oauth.domain.user.dto.UserAccessResponse
import io.playce.oauth.domain.user.dto.UserRoleResponse
import io.playce.oauth.domain.user.dto.UserSaveResponse
import io.playce.oauth.domain.user.service.UserAccessService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * <pre>
 *
 *  사용자 계정 관련 컨트롤러
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
class UserAccessController(
    private val userAccessService: UserAccessService,
) {

    /**
     * 사용자 목록 조회
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users")
    fun getUsers(@RequestHeader(value = "Authorization") accessToken: String): List<UserAccessResponse> {
        return userAccessService.getUsers()
    }

    /**
     * 사용자 상세 조회
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{userId}")
    fun getUser(@RequestHeader(value = "Authorization") accessToken: String, @PathVariable userId: Long): UserAccessResponse {
        return userAccessService.getUser(userId)
    }

    /**
     * 사용자 등록
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/users")
    fun createUser(@RequestHeader(value = "Authorization") accessToken: String,
                   @Valid @RequestBody userAccessRequest: UserAccessRequest): UserSaveResponse {
        return userAccessService.craeteUser(userAccessRequest)
    }

    /**
     * 사용자 수정
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/users/{userId}")
    fun modifyUser(@RequestHeader(value = "Authorization") accessToken: String,
                   @PathVariable userId: Long,
                   @Valid @RequestBody userAccessRequest: UserAccessRequest): UserSaveResponse {
        return userAccessService.modifyUser(accessToken, userId, userAccessRequest)
    }

    /**
     * 사용자 삭제
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/users/{userId}")
    fun deleteUser(@RequestHeader(value = "Authorization") accessToken: String, @PathVariable userId: Long): UserAccessResponse {
        return userAccessService.deleteUser(userId)
    }

    /**
     * 사용자별 권한 목록 조회
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{userId}/user-roles")
    fun getUserRoles(@RequestHeader(value = "Authorization") accessToken: String, @PathVariable userId: Long): List<UserRoleResponse> {
        return userAccessService.getUserRoles(userId)
    }
}