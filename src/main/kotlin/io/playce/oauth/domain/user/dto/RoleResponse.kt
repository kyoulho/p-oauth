package io.playce.oauth.domain.user.dto


/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */
data class RoleResponse (
    val userRoleId: Long,
    val userRoleName: String,
    val count: Int,
    val description: String,
) {
}