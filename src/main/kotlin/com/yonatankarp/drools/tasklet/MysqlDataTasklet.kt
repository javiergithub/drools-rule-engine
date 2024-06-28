package com.yonatankarp.drools.tasklet

import com.yonatankarp.drools.model.DataGetter
import com.yonatankarp.drools.model.SharedData
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

@Component
class MysqlDataTasklet (
    private val dataGetter: DataGetter,
    private val sharedData: SharedData
) : Tasklet {
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val mysqlData = dataGetter.getDataFromMysqlDB()
        sharedData.addMysqlData(mysqlData)
        return RepeatStatus.FINISHED
    }
}