package io.playce.oauth.entity

import io.playce.oauth.util.BooleanToYNConverter
import java.util.*
import javax.persistence.*

@Entity
class UserAccess(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userId: Long? = null,

    @Column(nullable = false)
    var username: String,

    @Column(nullable = false)
    var password: String,

    @Column(unique = true, nullable = false)
    var userLoginId: String,

    var loginFailureCnt: Int = 0,

    @Convert(converter = BooleanToYNConverter::class)
    var lockYn: Boolean = false,

    @Column(unique = true, nullable = true)
    var email: String,

    @Column(nullable = true)
    var organization: String,

    @Column(nullable = true)
    var jobTitle: String,

    @Column(nullable = false)
    var adminChangeYn: String,

    @Column(nullable = true)
    var deleteYn: String,

    @Column(nullable = true)
    var registDatetime: Date? = null,

    @Column(nullable = true)
    var modifyDatetime: Date? = null,

    @OneToMany(mappedBy = "userId")
    val userAccessRoleLinks: MutableSet<UserAccessRoleLink> = mutableSetOf()
)
