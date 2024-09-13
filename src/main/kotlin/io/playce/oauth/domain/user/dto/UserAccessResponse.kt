package io.playce.oauth.domain.user.dto

import java.util.*

/**
 * <pre>
 *
 * 사용자 계정 관련 response DTO
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */
data class UserAccessResponse (
    val userId: Long?,
    val userLoginId: String,
    val username: String,
    val email: String,
    val jobTitle: String,
    val organization: String,
    val userRoleId: Long,
    val userRoleName: String,
    val registDatetime: Date?,
    val lastActivityDatetime: Date?
)