package io.playce.oauth.config

import dev.akkinoc.util.YamlResourceBundle
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.nio.charset.StandardCharsets
import java.util.*

@Configuration
class WebMvcConfig
    : WebMvcConfigurer {

    @Bean
    fun localeResolver(): LocaleResolver {
        return AcceptHeaderLocaleResolver().also {
            it.defaultLocale = Locale.ENGLISH
        }
    }

    @Bean
    fun messageSource(@Value("\${spring.messages.basename}") basename: String): MessageSource {
        return YamlMessageSource().also {
            it.setBasename(basename)
            it.setDefaultEncoding(StandardCharsets.UTF_8.name())
            it.setUseCodeAsDefaultMessage(true)
            it.setAlwaysUseMessageFormat(true)
            it.setFallbackToSystemLocale(true)
        }
    }

    class YamlMessageSource : ResourceBundleMessageSource() {
        @Throws(MissingResourceException::class)
        override fun doGetBundle(basename: String, locale: Locale): ResourceBundle {
            return ResourceBundle.getBundle(basename, locale, YamlResourceBundle.Control)
        }
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/").setViewName("forward:/index.html")
        registry.addViewController("/{x:console}").setViewName("forward:/index.html")
        registry.addViewController("/{x:console}/").setViewName("forward:/index.html")
        registry.addViewController("/{x:console}/**/{y:[\\w\\-]+}").setViewName("forward:/index.html")
    }

}