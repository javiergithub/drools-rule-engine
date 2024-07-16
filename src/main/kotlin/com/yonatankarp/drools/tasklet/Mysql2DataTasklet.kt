package com.yonatankarp.drools.tasklet

import com.yonatankarp.drools.model.DataGetter
import com.yonatankarp.drools.model.SharedData
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

@Component
class Mysql2DataTasklet (
    private val dataGetter: DataGetter,
    private val sharedData: SharedData
) : Tasklet {
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val mysql2Data = dataGetter.getDataFromMysql2DB()
        sharedData.addMysql2Data(mysql2Data)
        return RepeatStatus.FINISHED
    }
}