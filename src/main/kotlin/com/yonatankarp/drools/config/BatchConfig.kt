package com.yonatankarp.drools.config

import com.yonatankarp.drools.model.DataGetter
import com.yonatankarp.drools.model.SharedData
import com.yonatankarp.drools.service.DataProcessor
import com.yonatankarp.drools.tasklet.MixWritingDataTasklet
import com.yonatankarp.drools.tasklet.Mysql2DataTasklet
import com.yonatankarp.drools.tasklet.MysqlDataTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.Step
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
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

@Configuration
class BatchConfig(
    @Autowired
    private val mysqlJobConfig: JobConfig,
    @Autowired
    private val sharedData: SharedData,
    @Autowired
    private val dataProcessing: DataProcessor,
    @Autowired
    private val dataGetter: DataGetter

) {
    @Bean
    fun readMysql2(): Step {
        return StepBuilder("readMysql2",mysqlJobConfig.JobRepository())
            .tasklet(Mysql2DataTasklet(dataGetter, sharedData), mysqlJobConfig.transactionManager())
            .transactionManager(mysqlJobConfig.transactionManager())
            .build()
    }

    @Bean
    fun readMysql(): Step {
        return StepBuilder("readMysql", mysqlJobConfig.JobRepository())
            .tasklet(MysqlDataTasklet(dataGetter, sharedData), mysqlJobConfig.transactionManager())
            .transactionManager(mysqlJobConfig.transactionManager())
            .startLimit(1)
            .build()
    }
    @Bean
    fun mixWritingData(): Step {
        return StepBuilder("mixWritingData", mysqlJobConfig.JobRepository())
            .tasklet(MixWritingDataTasklet(sharedData, dataProcessing), mysqlJobConfig.transactionManager())
            .startLimit(1)
            .build()
    }

    @Bean
    fun dataProcessingJob(@Autowired myEventListener: MyEventListener): Job {
        return JobBuilder("dataProcessingJob", mysqlJobConfig.JobRepository())
            .incrementer(RunIdIncrementer())
            .listener(myEventListener)
            .start(splitFlow())
            .next(mixMysqlFlow())
            .build()
            .build()
    }
    private fun readMysql2Flow(): Flow {
        return FlowBuilder<SimpleFlow>("readMysql2Flow")
            .start(readMysql2())
            .build()
    }
    private fun readMysqlFlow(): Flow {
        return FlowBuilder<SimpleFlow>("readMysqlFlow")
            .start(readMysql())
            .build()
    }

    private fun mixMysqlFlow(): Flow {
        return FlowBuilder<SimpleFlow>("mixMysqlFlow")
            .start(mixWritingData())
            .build()
    }

    private fun splitFlow(): Flow {
        return FlowBuilder<SimpleFlow>("splitFlow")
            .split(taskExecutor())
            .add(readMysqlFlow(),readMysql2Flow())
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
    class MyEventListener(@Autowired @Qualifier("threadPoolTaskExecutor") val taskExecutor: ThreadPoolTaskExecutor): JobExecutionListener {
        override fun afterJob(jobExecution: JobExecution) {
            this.taskExecutor.shutdown()
        }
    }
}