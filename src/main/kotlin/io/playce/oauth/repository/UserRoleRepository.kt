package io.playce.oauth.repository

import io.playce.oauth.entity.UserRole
import org.springframework.data.jpa.repository.JpaRepository

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */
interface UserRoleRepository : JpaRepository<UserRole, Long> {
    fun findByUserRoleId(userRoleId: Long?): UserRole
}