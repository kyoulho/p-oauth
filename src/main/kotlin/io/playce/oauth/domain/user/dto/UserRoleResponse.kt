package io.playce.oauth.domain.user.dto

import io.playce.oauth.entity.UserRole

/**
 * <pre>
 *
 * 사용자 권한 관련 response DTO
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */
data class UserRoleResponse (
    val userRoleId: Long,
    val userRoleName: String,
    val description: String,
) {
    companion object {
        fun fromUserRole(userRole: UserRole): UserRoleResponse {
            return UserRoleResponse(
                userRole.userRoleId,
                userRole.userRoleName,
                userRole.description,
            )
        }
    }
}