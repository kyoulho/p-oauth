package io.playce.oauth.domain.user.dto

import java.util.*


/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */
data class RoleUserResponse (
    val userId: Long?,
    val userLoginId: String,
    val username: String,
    val registDatetime: Date?,
    val lastActivityDatetime: Date?
) {
}