package io.playce.oauth.exception

import io.playce.oauth.config.logger
import io.playce.oauth.dto.ErrorResponse
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.io.PrintWriter
import java.io.StringWriter

@RestControllerAdvice
class ExceptionHandler(
    private val messageSource: MessageSource,
) {
    @ExceptionHandler(PlayceOAuthException::class)
    protected fun handlePlayceMigratorException(e: PlayceOAuthException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger().error("PlayceOAuthException occurred while execute [${request.getDescription(false)}] ", e)
        return newResponseEntity(e)
    }

    fun newResponseEntity(e: PlayceOAuthException): ResponseEntity<ErrorResponse> {
        val errorCode = e.errorCode
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        val defaultMessage = messageSource.getMessage(errorCode.name, e.arguments, e.message, LocaleContextHolder.getLocale())!!
        val errorResponse = ErrorResponse(errorCode.name, defaultMessage, sw.toString())
        logger().error("PlayceOAuthException's errorResponse is [${errorResponse}]")
        return ResponseEntity(errorResponse, e.httpStatus)
    }

}