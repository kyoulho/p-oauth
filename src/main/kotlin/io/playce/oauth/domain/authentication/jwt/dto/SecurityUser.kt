package io.playce.oauth.domain.authentication.jwt.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class SecurityUser (
    val userId: Long?,
    val userLonginId: String,
    private val username: String,
    @JsonIgnore
    private val password: String?,
    var authorities: MutableSet<out GrantedAuthority>?,
    val adminChangeYn: String,
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? {
        return authorities
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String {
        return this.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}