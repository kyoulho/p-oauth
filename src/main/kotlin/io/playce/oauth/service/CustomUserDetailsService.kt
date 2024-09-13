package io.playce.oauth.service

import io.playce.oauth.domain.authentication.dto.CustomUserDetails
import io.playce.oauth.repository.UserAccessRepository
import io.playce.oauth.repository.UserRoleRepository
import io.playce.oauth.repository.findByUserLoginIdOrThrow
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

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
class CustomUserDetailsService (
    private val userAccessRepository: UserAccessRepository,
    private val userRoleRepository: UserRoleRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val userAccess = userAccessRepository.findByUserLoginIdOrThrow(username)
        return CustomUserDetails(
            userId = userAccess.userId,
            userLonginId = userAccess.userLoginId,
            username = userAccess.username,
            password = userAccess.password,
            authorities = userAccess.userAccessRoleLinks.map {
                SimpleGrantedAuthority(userRoleRepository.findByUserRoleId(it.userRoleId).userRoleCode)
            }.toMutableSet(),
            loginFailureCnt = userAccess.loginFailureCnt,
            lockYn = userAccess.lockYn,
            adminChangeYn = userAccess.adminChangeYn,
            deleteYn = userAccess.deleteYn
        )
    }
}