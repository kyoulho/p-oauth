package io.playce.oauth.entity

import org.springframework.security.core.GrantedAuthority
import javax.persistence.*

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */
@Entity
class UserRole (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userRoleId: Long,

    @Column(nullable = false)
    var userRoleCode: String,

    @Column(nullable = false)
    var userRoleName: String,

    @Column(nullable = true)
    var description: String,

    ) : GrantedAuthority {
        override fun getAuthority(): String {
            return userRoleCode
    }
}