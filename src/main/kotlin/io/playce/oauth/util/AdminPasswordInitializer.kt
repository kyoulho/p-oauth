package io.playce.oauth.util

import io.playce.oauth.config.logger
import io.playce.oauth.repository.UserAccessRepository
import io.playce.oauth.repository.findByUserLoginIdOrThrow
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class AdminPasswordInitializer(
    private val userAccessRepository: UserAccessRepository,
) {
    @PostConstruct
    fun initPassword() {
        val value = System.getProperty("playce.admin.password.reset") ?: return
        if (value.isNotEmpty()) {
            logger().debug(":+:+:+:+:   Initialize for Reset Admin Password.   :+:+:+:+:")
            try {
                val userAccess = userAccessRepository.findByUserLoginIdOrThrow("admin")
                userAccess.password = "{bcrypt}" + BCryptPasswordEncoder().encode(value)
                userAccess.loginFailureCnt = 0
                userAccess.lockYn = false
                userAccessRepository.save(userAccess)
            } catch (e: Exception) {
                logger().error("Unhandled exception occurred while reset password. [Reason] : ", e)
            }
        }
    }
}