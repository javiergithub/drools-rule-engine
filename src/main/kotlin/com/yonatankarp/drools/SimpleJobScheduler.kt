
package com.yonatankarp.drools

import com.yonatankarp.drools.config.BatchConfig
import com.yonatankarp.drools.config.BatchConfig.MyEventListener
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Time
import java.text.SimpleDateFormat


@Component
//@ConditionalOnProperty(prefix = "schedule", name = ["active"], havingValue = "true")
class SimpleJobScheduler(
    val jobLauncher: JobLauncher,
    val simpleJobConfig: BatchConfig,
    val myEventListener: MyEventListener

) {
    private val logger: Log = LogFactory.getLog(SimpleJobScheduler::class.java)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    @Scheduled(initialDelay = 10000, fixedDelay = 30000)
    fun runJob(){
        val jobConf = hashMapOf<String, JobParameter<*>>()
        val jobParametersBuilder: JobParametersBuilder = JobParametersBuilder()
        jobParametersBuilder.addLong("time",System.currentTimeMillis())
     //   jobConf["time"] = JobParameter<Time>(dateFormat.format(System.currentTimeMillis()))
        val jobParameters = jobParametersBuilder.toJobParameters()

        try{
            jobLauncher.run(simpleJobConfig.dataProcessingJob(myEventListener), jobParameters)
        } catch(e: JobExecutionAlreadyRunningException){
            logger.error(e.localizedMessage)
        } catch(e: JobParametersInvalidException){
            logger.error(e.localizedMessage)
        }
    }
}