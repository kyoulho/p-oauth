package io.playce.oauth.domain.authentication.jwt.extractor

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */
interface TokenExtractor {
    fun extract(payload: String): String
}