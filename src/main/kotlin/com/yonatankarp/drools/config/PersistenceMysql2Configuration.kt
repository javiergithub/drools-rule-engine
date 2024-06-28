package com.yonatankarp.drools.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager


@Configuration
@PropertySource("classpath:application.properties")
@EnableJpaRepositories(
    basePackages = ["com.yonatankarp.drools.dao.mysql2"],
    entityManagerFactoryRef = "mysql2EntityManager",
    transactionManagerRef = "mysql2TransactionManager"
)
class PersistenceMysql2Configuration (@Autowired val env : Environment,
                                      @Autowired private val databaseConfig: DataSourceConfig){
    @Bean
    fun mysql2EntityManager(): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = databaseConfig.mysql2DataSource()
        em.setPackagesToScan(
            *arrayOf("com.yonatankarp.drools.model.mysql2")
        )

        val vendorAdapter = HibernateJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter
        val properties = HashMap<String, Any?>()
        properties["hibernate.hbm2ddl.auto"] = env.getProperty("spring.jpa.hibernate.ddl-auto")
        properties["hibernate.dialect"] = env.getProperty("spring.jpa.properties.hibernate.dialect")
        em.setJpaPropertyMap(properties)

        return em
    }

    @Bean
    fun mysql2TransactionManager(): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = mysql2EntityManager().getObject()
        return transactionManager
    }
}

