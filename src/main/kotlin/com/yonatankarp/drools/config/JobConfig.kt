package com.yonatankarp.drools.config

//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
//import org.springframework.batch.core.repository.JobRepository
//import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean
//import org.springframework.batch.support.DatabaseType
//import org.springframework.jdbc.datasource.DataSourceTransactionManager
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean
import org.springframework.batch.support.DatabaseType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource


//import org.springframework.transaction.TransactionManager
//import javax.sql.DataSource


@Configuration
//@EnableBatchProcessing
class JobConfig : DefaultBatchConfiguration() {

    @Throws(Exception::class)
    protected fun createJobRepository(): JobRepository {
        val factory = JobRepositoryFactoryBean()
        factory.setDatabaseType(DatabaseType.H2.productName)
        factory.setDataSource(dataSource())
        factory.transactionManager = transactionManager
        return factory.getObject()
    }

/*    @Primary
    fun transactionManager() : PlatformTransactionManager {
        return getTransactionManager()
    }*/
    @Bean
    fun transactionManager(emf: EntityManagerFactory?): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = emf
        return transactionManager
    }

    @Primary
    fun dataSource(): DataSource {
        val embeddedDatabaseBuilder = EmbeddedDatabaseBuilder()
        return embeddedDatabaseBuilder
            .addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
            .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
            .setType(EmbeddedDatabaseType.H2).build()
    }
}