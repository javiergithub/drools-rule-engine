package com.yonatankarp.drools.config

//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
//import org.springframework.batch.core.repository.JobRepository
//import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean
//import org.springframework.batch.support.DatabaseType
import org.springframework.context.annotation.Configuration
//import org.springframework.jdbc.datasource.DataSourceTransactionManager
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.transaction.PlatformTransactionManager
//import org.springframework.transaction.TransactionManager
//import javax.sql.DataSource


@Configuration
//@EnableBatchProcessing
class JobConfig(/*private val transactionManager: DataSourceTransactionManager*/) : DefaultBatchConfiguration() {

    /*@Throws(Exception::class)
    protected fun createJobRepository(): JobRepository {
        val factory = JobRepositoryFactoryBean()
        factory.setDatabaseType(DatabaseType.H2.productName)
        factory.setDataSource(dataSource())
        factory.transactionManager = getTransactionManager()
        return factory.getObject()
    }*/

    fun transactionManager() : PlatformTransactionManager {
        return getTransactionManager()
    }
 /*   fun dataSource(): DataSource {
        val embeddedDatabaseBuilder = EmbeddedDatabaseBuilder()
        return embeddedDatabaseBuilder
            .addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
            .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
            .setType(EmbeddedDatabaseType.H2).build()
    }*/
}