package io.playce.oauth.domain.authentication.jwt.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.playce.oauth.dto.ErrorResponse
import io.playce.oauth.exception.PlayceOAuthException
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * <pre>
 *
 *
 * </pre>
 *
 * @author Jihyun Park
 * @version 1.0
 */


class ExceptionHandlerFilter (
    private val messageSource: MessageSource,
    private val objectMapper: ObjectMapper,
    ): OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: PlayceOAuthException) {
            sendErrorResponse(request, response, e)
        }
    }

    @Throws(IOException::class)
    private fun sendErrorResponse(
        request: HttpServletRequest,
        response: HttpServletResponse,
        e: PlayceOAuthException
    ) {
        val errorCode = e.errorCode
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))

        val defaultMessage = messageSource.getMessage(errorCode.name, null, e.message, LocaleContextHolder.getLocale())!!
        val errorResponse = ErrorResponse(errorCode.name, defaultMessage, sw.toString())
        response.status = e.httpStatus.value()
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.contentType = "application/json"
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
