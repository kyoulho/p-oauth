package io.playce.oauth.domain.user.service

import io.playce.oauth.domain.user.dto.RoleResponse
import io.playce.oauth.domain.user.dto.RoleUserResponse
import io.playce.oauth.entity.UserRole
import io.playce.oauth.exception.ErrorCode
import io.playce.oauth.exception.PlayceOAuthException
import io.playce.oauth.repository.UserAccessRepository
import io.playce.oauth.repository.UserAccessRoleLinkRepository
import io.playce.oauth.repository.UserRoleRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
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

@Service
class UserRoleService (
    private val userRoleRepository: UserRoleRepository,
    private val userAccessRoleLinkRepository: UserAccessRoleLinkRepository,
    private val userAccessRepository: UserAccessRepository,
){

    /**
     * 권한 목록 조회
     */
    @Transactional(readOnly = true)
    fun getUserRoles(): List<RoleResponse> {
        return userRoleRepository.findAll()
            .map { role -> mapUserRoleToRoleResponse(role) }
    }

    /**
     * 권한 상세 조회
     */
    @Transactional(readOnly = true)
    fun getUserRole(userRoleId: Long): RoleResponse {
        return userRoleRepository.findById(userRoleId)
            .map { role -> mapUserRoleToRoleResponse(role) }
            .orElseThrow{ PlayceOAuthException(ErrorCode.NOT_FOUND_USER_ROLE, HttpStatus.NOT_FOUND) }
    }

    private fun mapUserRoleToRoleResponse(role: UserRole): RoleResponse {
        val userIds = userAccessRoleLinkRepository.findByUserRoleId(role.userRoleId).map { it.userId }
        val userCount = userAccessRepository.countByUserIdInAndDeleteYn(userIds, "N")
        return  RoleResponse(
            userRoleId = role.userRoleId,
            userRoleName = role.userRoleName,
            count = userCount,
            description = role.description
        )
    }

    /**
     * 권한별 사용자 목록 조회
     */
    fun getUsersByRoles(userRoleId: Long): List<RoleUserResponse> {
        val userIds = userAccessRoleLinkRepository.findByUserRoleId(userRoleId).map { it.userId }
        return userAccessRepository.findAllByUserIdInAndDeleteYnOrderByUserIdDesc(userIds, "N")
            .map { user -> RoleUserResponse (
                    userId = user.userId,
                    userLoginId = user.userLoginId,
                    username = user.username,
                    registDatetime = user.registDatetime,
                    lastActivityDatetime = null
                )
            }
    }

}