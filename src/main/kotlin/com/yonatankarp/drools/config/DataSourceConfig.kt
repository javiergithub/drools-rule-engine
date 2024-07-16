package com.yonatankarp.drools.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Configuration
class DataSourceConfig(@Autowired val env : Environment) {

    @Bean(name = ["mysql2DataSource"])
    @ConfigurationProperties(prefix = "spring.datasource.mysql2")
    fun mysql2DataSource(): DataSource {
        return DataSourceBuilder.create()
            .url(env.getProperty("spring.datasource.mysql2.url"))
            .driverClassName(env.getProperty("spring.datasource.mysql2.driver-class-name"))
            .username(env.getProperty("spring.datasource.mysql2.username"))
            .password(env.getProperty("spring.datasource.mysql2.password"))
            .build()
    }

    @Bean(name = ["dataSource"])
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    fun mysqlDataSource(): DataSource {
        return DataSourceBuilder.create()
            .url(env.getProperty("spring.datasource.mysql.url"))
            .driverClassName(env.getProperty("spring.datasource.mysql.driver-class-name"))
            .username(env.getProperty("spring.datasource.mysql.username"))
            .password(env.getProperty("spring.datasource.mysql.password"))
            .build()
    }
}