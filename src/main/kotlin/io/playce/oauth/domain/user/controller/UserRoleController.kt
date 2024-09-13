package io.playce.oauth.domain.user.controller

import io.playce.oauth.domain.user.dto.RoleResponse
import io.playce.oauth.domain.user.dto.RoleUserResponse
import io.playce.oauth.domain.user.service.UserRoleService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * <pre>
 *
 * 사용자 권한 관련 컨트롤러
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */

@RestController
@RequestMapping("/api")
class UserRoleController(
    private val userRoleService: UserRoleService,
) {

    /**
     * 권한 목록 조회
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user-roles")
    fun getUserRoles(@RequestHeader(value = "Authorization") accessToken: String): List<RoleResponse> {
        return userRoleService.getUserRoles()
    }

    /**
     * 권한 단건 조회
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user-roles/{userRoleId}")
    fun getUserRole(@RequestHeader(value = "Authorization") accessToken: String, @PathVariable userRoleId: Long): RoleResponse {
        return userRoleService.getUserRole(userRoleId)
    }

    /**
     * 권한별 사용자 목록 조회
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user-roles/{userRoleId}/users")
    fun getUsersByRoles(@RequestHeader(value = "Authorization") accessToken: String, @PathVariable userRoleId: Long): List<RoleUserResponse> {
        return userRoleService.getUsersByRoles(userRoleId)
    }

}