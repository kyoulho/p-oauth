package io.playce.oauth.domain.user.dto

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * <pre>
 *
 * 사용자 계정 관련 request DTO
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */
data class UserAccessRequest(

    @field:NotEmpty
    val userLoginId: String,

    val password: String?,

    @field:NotEmpty
    val username: String,

    val email: String,

    val jobTitle: String,

    val organization: String,

    @field:NotNull
    val userRoleId: Long,
)
