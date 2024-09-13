package io.playce.oauth.repository

import io.playce.oauth.entity.UserAccessRoleLink
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
interface UserAccessRoleLinkRepository : JpaRepository<UserAccessRoleLink, Long> {
   fun deleteByUserId(userId: Long?)
   fun findByUserId(userId: Long) : List<UserAccessRoleLink>
   fun findByUserRoleId(userRoleId: Long) : List<UserAccessRoleLink>
}