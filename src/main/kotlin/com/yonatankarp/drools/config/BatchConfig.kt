package com.yonatankarp.drools.config

import com.yonatankarp.drools.tasklet.MysqlDataTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
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
            .tasklet(MysqlDataTasklet(), mysqlJobConfig.transactionManager(null))
            .transactionManager(mysqlJobConfig.transactionManager( null ))
            .startLimit(1)
            .build()
    }


    private fun readMysqlFlow(): Flow {
        return FlowBuilder<SimpleFlow>("readMysqlFlow")
            .start(readMysql())
            .build()
    }


    @JobScope
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



    @JobScope
    @Bean("threadPoolTaskExecutor")
    fun taskExecutor(): ThreadPoolTaskExecutor {
        val taskExecutor = ThreadPoolTaskExecutor()
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true)
        taskExecutor.setAwaitTerminationSeconds(10)
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