package com.yonatankarp.drools.config

import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager

@Configuration
@PropertySource("classpath:application.properties")
@EnableJpaRepositories(
    basePackages = ["com.yonatankarp.drools.dao.mysql"],
    entityManagerFactoryRef = "mysqlEntityManager",
    transactionManagerRef = "transactionManager"
)
class JobConfig(@Autowired val env : Environment,
                @Autowired private val databaseConfig: DataSourceConfig) {

    @Bean
    fun JobRepository(): JobRepository {
        val jobRepositoryFactoryBean = JobRepositoryFactoryBean()
        jobRepositoryFactoryBean.setDatabaseType("MYSQL")
        jobRepositoryFactoryBean.setDataSource(databaseConfig.mysqlDataSource())
        jobRepositoryFactoryBean.transactionManager = transactionManager()
        jobRepositoryFactoryBean.setIsolationLevelForCreate("ISOLATION_READ_UNCOMMITTED")
        jobRepositoryFactoryBean.afterPropertiesSet()
        return jobRepositoryFactoryBean.`object`
    }

/*    @Bean(name = ["transactionManager"])
    @Primary
    fun transactionManager(): PlatformTransactionManager {
        return DataSourceTransactionManager(databaseConfig.mysqlDataSource())
    }*/

    @Bean
    fun mysqlEntityManager(): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = databaseConfig.mysqlDataSource()
        em.setPackagesToScan(
            *arrayOf("com.yonatankarp.drools.model.mysql")
        )

        val vendorAdapter = HibernateJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter
        val properties = HashMap<String, Any?>()
        properties["hibernate.hbm2ddl.auto"] = env.getProperty("spring.jpa.hibernate.ddl-auto")
        properties["hibernate.dialect"] = env.getProperty("spring.jpa.properties.hibernate.dialect")
        em.setJpaPropertyMap(properties)

        return em
    }

    @Bean(name = ["transactionManager"])
    @Primary
    fun transactionManager(): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = mysqlEntityManager().getObject()
        return transactionManager
    }

}