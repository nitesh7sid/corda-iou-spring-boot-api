package com.hmlr.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author Nitesh Solanki
 */


/**
 * Our Spring Boot application.
 */
@SpringBootApplication
open class Application

/**
 * Starts our Spring Boot application.
 */
fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}