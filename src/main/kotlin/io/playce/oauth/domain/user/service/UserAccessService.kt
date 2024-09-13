package io.playce.oauth.domain.user.service

import io.playce.oauth.domain.user.dto.UserAccessRequest
import io.playce.oauth.domain.user.dto.UserAccessResponse
import io.playce.oauth.domain.user.dto.UserRoleResponse
import io.playce.oauth.domain.user.dto.UserSaveResponse
import io.playce.oauth.entity.UserAccess
import io.playce.oauth.entity.UserAccessRoleLink
import io.playce.oauth.exception.ErrorCode
import io.playce.oauth.exception.PlayceOAuthException
import io.playce.oauth.repository.UserAccessRepository
import io.playce.oauth.repository.UserAccessRoleLinkRepository
import io.playce.oauth.repository.UserRoleRepository
import io.playce.oauth.repository.findByUserLoginIdOrThrow
import io.playce.oauth.util.GeneralCipherUtil
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserAccessService(
    private val userAccessRepository: UserAccessRepository,
    private val userRoleRepository: UserRoleRepository,
    private val userAccessRoleLinkRepository: UserAccessRoleLinkRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    private val maxFailCount = 7

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun resetLoginFailureCnt(userLoginId: String) {
        userAccessRepository.findByUserLoginIdOrThrow(userLoginId)
            .also {
                it.loginFailureCnt = 0
            }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateLoginFailureCnt(userLoginId: String) {
        userAccessRepository.findByUserLoginIdOrThrow(userLoginId)
            .also {
                it.loginFailureCnt++
                if (it.loginFailureCnt >= maxFailCount) {
                    it.lockYn = true
                }
            }
    }

    /**
     * 사용자 목록 조회
     */
    @Transactional(readOnly = true)
    fun getUsers(): List<UserAccessResponse> {
        val userList = userAccessRepository.findAllByDeleteYnOrderByUserIdDesc("N")
        return userList.map { user -> mapUserToUserResponse(user) }
    }

    /**
     * 사용자 조회
     */
    @Transactional(readOnly = true)
    fun getUser(userId: Long): UserAccessResponse {
        val user = userAccessRepository.findById(userId).filter { it.deleteYn == "N" }
            .orElseThrow { PlayceOAuthException(ErrorCode.NOT_FOUND_USER, HttpStatus.NOT_FOUND) }
        return mapUserToUserResponse(user)
    }

    private fun mapUserToUserResponse(userAccess: UserAccess): UserAccessResponse {
        // TODO: ui 기획상 사용자 권한이 1개 fix라서 임시로 한개의 권한만 조회하도록 처리, 추후 변경 필요
        val userRoleLink = userAccess.userAccessRoleLinks.first()
        val userRole = userRoleRepository.findByUserRoleId(userRoleLink.userRoleId)
        return UserAccessResponse(
            userId = userAccess.userId,
            userLoginId = userAccess.userLoginId,
            username = userAccess.username,
            email = userAccess.email,
            jobTitle = userAccess.jobTitle,
            organization = userAccess.organization,
            userRoleId = userRole.userRoleId,
            userRoleName = userRole.userRoleName,
            registDatetime = userAccess.registDatetime,
            lastActivityDatetime = null
        )
    }


    /**
     * 사용자 등록
     */

    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Transactional
    fun craeteUser(userAccessRequest: UserAccessRequest): UserSaveResponse{

        // 로그인 아이디 중복 체크
        if (userAccessRepository.existsByUserLoginId(userAccessRequest.userLoginId)) {
            throw PlayceOAuthException(ErrorCode.DUPLICATED_USER_LOGIN_ID, HttpStatus.BAD_REQUEST, userAccessRequest.userLoginId)
        }

        // 이메일 중복체크
        if (userAccessRequest.email.isNotEmpty() && userAccessRepository.existsByEmail(userAccessRequest.email)) {
            throw PlayceOAuthException(ErrorCode.DUPLICATED_EMAIL, HttpStatus.BAD_REQUEST, userAccessRequest.email)
        }

        var adminChangeYn = ""
        var password = ""
        userAccessRequest.password?.let{ p ->
            password = GeneralCipherUtil.decrypt(p)
            adminChangeYn = checkPasswordEqualToId(userAccessRequest.userLoginId, GeneralCipherUtil.decrypt(p))
        }

        val userAccess = UserAccess(
            username = userAccessRequest.username,
            password = passwordEncoder.encode(password),
            userLoginId = userAccessRequest.userLoginId,
            email = userAccessRequest.email,
            organization = userAccessRequest.organization,
            jobTitle = userAccessRequest.jobTitle,
            adminChangeYn = adminChangeYn,
            deleteYn = "N",
            registDatetime = Date(),
        )

        userAccessRepository.save(userAccess)
        // 사용자권한링크 저장
        userAccessRoleLinkRepository.save(UserAccessRoleLink(userId = userAccess.userId, userRoleId = userAccessRequest.userRoleId))
        return UserSaveResponse(userAccess.userId, userAccess.userLoginId)
    }

    @Transactional
    fun modifyUser(accessToken: String, userId:Long, userAccessRequest: UserAccessRequest): UserSaveResponse {
        // 사용자 존재 여부
        val user =  userAccessRepository.findById(userId)
            .orElseThrow { PlayceOAuthException(ErrorCode.NOT_FOUND_USER, HttpStatus.NOT_FOUND) }

        // 로그인 아이디 중복 체크
        if (userAccessRepository.existsByUserLoginIdAndUserIdNot(userAccessRequest.userLoginId, userId)) {
            throw PlayceOAuthException(ErrorCode.DUPLICATED_USER_LOGIN_ID, HttpStatus.BAD_REQUEST, userAccessRequest.userLoginId)
        }

        // 이메일 중복체크
        if (userAccessRequest.email.isNotEmpty() && userAccessRepository.existsByEmailAndUserIdNot(userAccessRequest.email, userId)) {
            throw PlayceOAuthException(ErrorCode.DUPLICATED_EMAIL, HttpStatus.BAD_REQUEST, userAccessRequest.email)
        }

        user.apply {
            userLoginId = userAccessRequest.userLoginId
            username = userAccessRequest.username
            email = userAccessRequest.email
            organization = userAccessRequest.organization
            jobTitle = userAccessRequest.jobTitle
            modifyDatetime = Date()
            userAccessRequest.password?.let { password ->
                val decryptPassword = GeneralCipherUtil.decrypt(password)
                this.password = passwordEncoder.encode(decryptPassword)
                adminChangeYn = checkPasswordEqualToId(userAccessRequest.userLoginId, decryptPassword)
            }

        }
        userAccessRoleLinkRepository.deleteByUserId(user.userId)
        // 사용자권한링크 저장
        userAccessRoleLinkRepository.save(UserAccessRoleLink(userId = user.userId, userRoleId = userAccessRequest.userRoleId))
        return UserSaveResponse(user.userId, user.userLoginId)
    }

    private fun checkPasswordEqualToId(userLoginId: String, password: String): String {
        return if(userLoginId == password) {
            "Y"
        } else {
            "N"
        }
    }


    /**
     * 사용자 삭제
     */
    @Transactional
    fun deleteUser(userId: Long): UserAccessResponse {
        val userAccess = userAccessRepository.findById(userId).filter{ it.deleteYn == "N"}
            .orElseThrow { PlayceOAuthException(ErrorCode.NOT_FOUND_USER, HttpStatus.NOT_FOUND) }
            .also {
                it.deleteYn = "Y"
                it.modifyDatetime = Date()
            }
        return mapUserToUserResponse(userAccess)
    }

    /**
     * 사용자별 권한 목록 조회
     */
    @Transactional(readOnly = true)
    fun getUserRoles(userId: Long): List<UserRoleResponse> {
        val userRoles = userAccessRoleLinkRepository.findByUserId(userId)
        return userRoles.map {
            UserRoleResponse.fromUserRole(userRoleRepository.findByUserRoleId(it.userRoleId))
        }

    }

}