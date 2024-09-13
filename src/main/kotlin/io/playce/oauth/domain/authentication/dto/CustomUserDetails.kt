package io.playce.oauth.domain.authentication.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    val userId: Long?,
    val userLonginId: String,
    private val username: String,
    @JsonIgnore
    private val password: String,
    val authorities: MutableSet<out GrantedAuthority>,
    val loginFailureCnt: Int,
    val lockYn: Boolean,
    val adminChangeYn: String,
    val deleteYn: String,
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return this.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return !lockYn
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return !lockYn
    }

    fun isDeleted(): Boolean {
        return deleteYn == "Y"
    }

}