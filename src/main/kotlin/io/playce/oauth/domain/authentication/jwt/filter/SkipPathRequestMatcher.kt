package io.playce.oauth.domain.authentication.jwt.filter

import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */

class SkipPathRequestMatcher(pathsToSkip: List<String?>, processingPath: String?) :
    RequestMatcher {
    private val matchers: OrRequestMatcher
    private val processingMatcher: RequestMatcher

    init {
        val m = pathsToSkip.stream().map { pattern: String? -> AntPathRequestMatcher(pattern) }.collect(Collectors.toList())
        matchers = OrRequestMatcher(m as List<RequestMatcher>?)
        processingMatcher = AntPathRequestMatcher(processingPath)
    }

    override fun matches(request: HttpServletRequest?): Boolean {
        return if (matchers.matches(request)) { false } else processingMatcher.matches(request)
    }
}
