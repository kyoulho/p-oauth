package io.playce.oauth

import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import javax.servlet.ServletContext

class ServletInitializer : SpringBootServletInitializer() {
    override fun onStartup(servletContext: ServletContext) {
        initDatabase()
        super.onStartup(servletContext)
    }

    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(PlayceOauthApplication::class.java)
    }
}
