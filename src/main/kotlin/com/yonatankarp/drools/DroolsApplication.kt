package com.yonatankarp.drools

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DroolsApplication

fun main(args: Array<String>) {
    runApplication<DroolsApplication>(*args)
}
