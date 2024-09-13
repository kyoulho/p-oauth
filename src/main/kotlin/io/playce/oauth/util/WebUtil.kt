package io.playce.oauth.util

import io.playce.oauth.domain.authentication.jwt.dto.SecurityUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */
abstract class WebUtil {

  companion object {
      fun getLoginUser(): UserDetails? {
          val authentication = SecurityContextHolder.getContext().authentication
          return if (authentication != null && authentication.principal is UserDetails) {
              authentication.principal as UserDetails
          } else null
      }

      fun getUsername(): String {
          val userDetails = getLoginUser()
          return if (userDetails != null) {
              (userDetails as SecurityUser).userLonginId
          } else "admin"
      }

      fun hasRole(role: String): Boolean {
          val context = SecurityContextHolder.getContext() ?: return false
          val authentication = context.authentication ?: return false
          for (auth in authentication.authorities) {
              if (role == auth.authority) {
                  return true
              }
          }
          return false
      }
  }

}
