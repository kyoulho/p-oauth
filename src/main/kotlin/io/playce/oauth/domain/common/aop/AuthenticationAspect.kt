package io.playce.oauth.domain.common.aop

import io.playce.oauth.config.logger
import io.playce.oauth.domain.authentication.dto.CustomUserDetails
import io.playce.oauth.exception.ErrorCode
import io.playce.oauth.exception.PlayceOAuthException
import io.playce.oauth.service.CustomUserDetailsService
import io.playce.oauth.util.WebUtil
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */

@Aspect
@Component
class AuthenticationAspect (
    private val customUserDetailsService: CustomUserDetailsService
) {

    @Before("execution(* io.playce.oauth.domain.*.controller.*Controller*.*(..)) ")
    @Throws(
        Throwable::class
    )
    fun loggerBefore(joinPoint: JoinPoint?) {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.request

        // 사용자 삭제 상태 체크
        if(checkDeletedUser(WebUtil.getUsername())) {
            throw PlayceOAuthException(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED)
        }
        if (!AUTH_URIS.contains(request.requestURI.replace("/oauth", ""))) {
            // Administrator가 아닌데, 사용자 관련 메뉴를 호출하는 경우
            if (!isAdmin(WebUtil.getUsername()) && joinPoint!=null) {
                // 개별 사용자 조회는 제외
                if (!joinPoint.signature.name.equals("getUser")) {
                    logger().debug("start - " + joinPoint.signature.declaringTypeName + " / " + joinPoint.signature.name)
                    logger().debug(request.getMethod());
                    logger().debug(request.getHeader("Authorization"))
                    logger().info("[{}] has no permission. Roles are {}", WebUtil.getUsername(), WebUtil.getLoginUser()?.authorities.toString())

                    throw PlayceOAuthException(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN)
                }

            }
        }
    }

    companion object {
        private val AUTH_URIS = arrayOf("/api/auth/login", "/api/auth/refresh-token", "/api/auth/change-password")
    }

    /**
     * 사용자 정보 조회
     */
    private fun getUserDetails(userLoginId: String): CustomUserDetails {
        return customUserDetailsService.loadUserByUsername((userLoginId)) as CustomUserDetails
    }

    /**
     * 삭제된 사용자인지 체크한다.
     */
    private fun checkDeletedUser(userLoginId: String): Boolean {
        return getUserDetails(userLoginId).isDeleted()
    }

    /**
     * 사용자가 admin인지 판단한다.
     */
    private fun isAdmin(userLoginId: String): Boolean {
        val authorities = getUserDetails(userLoginId).getAuthorities()
        return authorities.any { it.authority == "ROLE_ADMIN" }
    }
 }
