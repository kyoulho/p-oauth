package io.playce.oauth

import io.playce.oauth.exception.ErrorCode
import io.playce.oauth.exception.PlayceOAuthException
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import kotlin.system.exitProcess

@ConfigurationPropertiesScan
@SpringBootApplication
class PlayceOauthApplication

fun main(args: Array<String>) {
    initDatabase()
    runApplication<PlayceOauthApplication>(*args)
}

fun initDatabase() {
    val jdbcUrl = System.getProperty("spring.datasource.url", "jdbc:mariadb://localhost:3306/oauthdb")
    val username = System.getProperty("spring.datasource.username", "playce")
    val password = System.getProperty("spring.datasource.password", "playce")

    if (!(jdbcUrl.contains("mysql") || jdbcUrl.contains("maria"))) {
        throw PlayceOAuthException(ErrorCode.INIT_DB_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    // DB 로깅을 비활성화한다.
    val url = jdbcUrl.replace("log4jdbc:".toRegex(), "")
    val driverClass = if (jdbcUrl.contains("mariadb")) "org.mariadb.jdbc.Driver" else "com.mysql.jdbc.Driver"

    try {
        Class.forName(driverClass).getDeclaredConstructor().newInstance()
        val tableNames = getTableNames(username, password, url)
        if (tableNames.isEmpty()) {
            println(Date().toString() + " : OAuth database not initialized. Starting auto configuration for OAuth database.")
            System.setProperty("spring.sql.init.mode", "always")
        } else {
            println(Date().toString() + " : OAuth database already initialized.")
        }
    } catch (e: PlayceOAuthException) {
        System.err.println("\nOAuth will be terminated. Please use MariaDB over 10.6.0.")
        System.err.println("[Connection URL] : $url")
        exitProcess(-1)
    } catch (e: SQLException) {
        System.err.println("\nOAuth will be terminated. Please check the DB connection information is valid.")
        System.err.println("[Connection URL] : $url")
        System.err.println("[Username] : $username")
        System.err.println("[Password] : $password")
        exitProcess(-1)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun getTableNames(username: String, password: String, url: String): List<String> {
    val tableNames: MutableList<String> = ArrayList()
    DriverManager.getConnection(url, username, password).use { connection ->
        connection.createStatement().use { stmt ->
            stmt.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = '" + connection.catalog + "';")
                .use { results ->
                    while (results.next()) {
                        tableNames.add(results.getString("table_name").uppercase(Locale.getDefault()))
                    }
                }
        }
    }
    return tableNames
}