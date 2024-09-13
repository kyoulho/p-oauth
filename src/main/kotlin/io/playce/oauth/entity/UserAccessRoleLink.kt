package io.playce.oauth.entity

import java.io.Serializable
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

class UserAccessRoleLinkId(
    val userId: Long? = null,
    val userRoleId: Long? = null
) : Serializable

@Entity
@IdClass(UserAccessRoleLinkId::class)
class UserAccessRoleLink (

    @Id
    val userId: Long? = null,

    @Id
    val userRoleId: Long,

): Serializable {

}

