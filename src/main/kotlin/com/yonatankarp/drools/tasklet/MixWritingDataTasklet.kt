package com.yonatankarp.drools.tasklet

import com.yonatankarp.drools.model.SharedData
import com.yonatankarp.drools.service.DataProcessor
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

//@Component
class MixWritingDataTasklet (
    private val sharedData: SharedData,
    private val dataProcessor: DataProcessor
) : Tasklet {
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        dataProcessor.combineData(sharedData.getMysql2Data(),  sharedData.getMysqlData())
        return RepeatStatus.FINISHED
    }
}