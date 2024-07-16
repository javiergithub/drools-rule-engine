package com.yonatankarp.drools.config

import com.yonatankarp.drools.tasklet.MysqlDataTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.FlowBuilder
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.job.flow.Flow
import org.springframework.batch.core.job.flow.support.SimpleFlow
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

@Configuration
class BatchConfig (
    @Autowired private val mysqlJobConfig: JobConfig
){

    @Bean
    fun readMysql(): Step {
        return StepBuilder("readMysql", mysqlJobConfig.jobRepository())
            .tasklet(MysqlDataTasklet(), mysqlJobConfig.transactionManager())
 //           .transactionManager(mysqlJobConfig.transactionManager())
            .startLimit(1)
            .build()
    }


    private fun readMysqlFlow(): Flow {
        return FlowBuilder<SimpleFlow>("readMysqlFlow")
            .start(readMysql())
            .build()
    }


    private fun splitFlow(): Flow {
        return FlowBuilder<SimpleFlow>("splitFlow")
            .split(taskExecutor())
            .add(readMysqlFlow())
            .build()
    }


    @Bean
    fun dataProcessingJob(myEventListener: MyEventListener): Job {
        return JobBuilder("dataProcessingJob", mysqlJobConfig.jobRepository())
            .incrementer(RunIdIncrementer())
            .listener(myEventListener)
            .start(splitFlow())
//            .next(mixMysqlFlow())
           .build()
            .build()

    }


    @Bean("threadPoolTaskExecutor")
    fun taskExecutor(): ThreadPoolTaskExecutor {
        val taskExecutor = ThreadPoolTaskExecutor()
        taskExecutor.corePoolSize = 2// Configure based on your needs
        taskExecutor.maxPoolSize = 4// Configure based on your needs
        taskExecutor.initialize()
        return taskExecutor
    }


    @Component
    class MyEventListener(@Autowired @Qualifier("threadPoolTaskExecutor") val taskExecutor: ThreadPoolTaskExecutor):
        JobExecutionListener {
        override fun afterJob(jobExecution: JobExecution) {
            this.taskExecutor.shutdown()
        }
    }

}