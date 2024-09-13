package io.playce.oauth.repository

import io.playce.oauth.entity.UserAccess
import io.playce.oauth.exception.ErrorCode
import io.playce.oauth.exception.PlayceOAuthException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.HttpStatus

interface UserAccessRepository : JpaRepository<UserAccess, Long> {
    fun findByUserLoginId(loginId: String): UserAccess?
    fun findAllByDeleteYnOrderByUserIdDesc(deleteYn: String): List<UserAccess>
    fun existsByUserLoginId(userLoginId: String): Boolean
    fun existsByEmail(email: String?): Boolean
    fun existsByUserLoginIdAndUserIdNot(userLoginId: String, userId: Long): Boolean
    fun existsByEmailAndUserIdNot(email: String?, userId: Long): Boolean
    fun findAllByUserIdInAndDeleteYnOrderByUserIdDesc(userIds: List<Long?>, deleteYn: String): List<UserAccess>
    fun countByUserIdInAndDeleteYn(userIds: List<Long?>, deleteYn: String): Int
}
fun UserAccessRepository.findByUserLoginIdOrThrow(loginId: String): UserAccess {
    return this.findByUserLoginId(loginId)
        ?: throw PlayceOAuthException(ErrorCode.NOT_FOUND_USER, HttpStatus.NOT_FOUND)
}
