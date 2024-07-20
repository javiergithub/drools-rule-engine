package com.yonatankarp.drools.config

import com.yonatankarp.drools.model.Trade
import com.yonatankarp.drools.processor.TradeDuckdbItemProcessor
import com.yonatankarp.drools.reader.FileToTradeDuckdbItemReader
import com.yonatankarp.drools.writer.ConsoleItemWriter
import com.yonatankarp.drools.writer.TradeDuckdbItemWriter
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.LineMapper
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.mapping.FieldSetMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.batch.item.file.transform.LineTokenizer
import org.springframework.batch.repeat.CompletionPolicy
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import javax.sql.DataSource


@Configuration
class TradeJobConfig (@Autowired private val mysqlJobConfig: JobConfig)  {

    @Autowired
    private val batchDataSource: DataSource? = null
    private val dbRuta = "/home/javier/win-d/var/timingest/db/"
    private val dbName = "tim_ingest"
    private val tradeLoadTable: String = "trade_load"
    private val dbPrefix: String = "duckdb_"

    @Bean
    @StepScope
    fun tradeItemReader(
        @Value("#{jobParameters['tradeFile']}") inputFile: String
    ): FlatFileItemReader<Trade> {
        val tradeLineMapper: LineMapper<Trade> = createTradeLineMapper()
        val tradeResource = FileSystemResource(inputFile);

        return FlatFileItemReaderBuilder<Trade>()
            .name("tradeItemReader")
            .lineMapper(tradeLineMapper)
            .resource(tradeResource)
            .linesToSkip(1)
            .build()
    }
    private fun createTradeLineMapper(): LineMapper<Trade> {
        val tradeLineMapper: DefaultLineMapper<Trade> = DefaultLineMapper<Trade>()

        val tradeLineTokenizer = createTradeLineTokenizer()
        tradeLineMapper.setLineTokenizer(tradeLineTokenizer)

        val tradeInformationMapper: FieldSetMapper<Trade> =
            createTradeInformationMapper()
        tradeLineMapper.setFieldSetMapper(tradeInformationMapper)

        return tradeLineMapper
    }

    private fun createTradeLineTokenizer(): LineTokenizer {
        val tradeLineTokenizer = DelimitedLineTokenizer()
        tradeLineTokenizer.setDelimiter(",")
        tradeLineTokenizer.setNames(
            *arrayOf(
                "id"
            )
        )

        return tradeLineTokenizer
    }

    private fun createTradeInformationMapper(): FieldSetMapper<Trade> {
        val tradeInformationMapper: BeanWrapperFieldSetMapper<Trade> =
            BeanWrapperFieldSetMapper<Trade>()
        tradeInformationMapper.setTargetType(Trade::class.java)
        return tradeInformationMapper
    }

    @Bean("fileToTradeDuckdbItemReader")
    @StepScope
    fun fileToTradeDuckdbItemReader(
        @Value("#{jobParameters['tradeFile']}") inputFile: String
    ): ItemReader<List<Trade>> {
       return FileToTradeDuckdbItemReader(inputFile,dbRuta,dbName,dbPrefix,tradeLoadTable)
    }


    @Bean
    @StepScope
    fun tradeConsoleItemWriter() : ItemWriter<Trade> {
        return ConsoleItemWriter<Trade>()
    }

    @Bean("tradeDuckdbItemProcessor")
    @StepScope
    fun tradeDuckerItemProcessor() : ItemProcessor<List<Trade>, List<Trade>> {
        return TradeDuckdbItemProcessor()
    }

    @Bean("tradeDuckdbItemWriter")
    @StepScope
    fun tradeDuckerItemWriter() : ItemWriter<List<Trade>> {
        return TradeDuckdbItemWriter()
    }


    @Bean
    fun readTradeFile( @Qualifier("fileToTradeDuckdbItemReader") tradeItemReader : ItemReader<List<Trade>>,
                       @Qualifier("tradeDuckdbItemProcessor") tradeItemProcessor : ItemProcessor<List<Trade>, List<Trade>>,
                       @Qualifier("tradeDuckdbItemWriter") tradeItemWriter : ItemWriter<List<Trade>>): Step {
        val stepBuilder : StepBuilder = StepBuilder("readTradeFile", mysqlJobConfig.jobRepository())
        return stepBuilder.chunk<List<Trade>,List<Trade>>(completionPolicy(), mysqlJobConfig.transactionManager(null))

            .startLimit(1)
            .reader(tradeItemReader)
            .processor(tradeItemProcessor)
            .writer(tradeItemWriter)
            .build()
    }

    @Bean
    fun tradeJob(
        readTradeFile: Step
    ): Job {
        return JobBuilder("tradeJob", mysqlJobConfig.jobRepository())
            .incrementer(RunIdIncrementer())
            .flow(readTradeFile)
            .build()
            .build()
    }
    @Bean
    fun completionPolicy(): CompletionPolicy {
        val policy =
            CompositeCompletionPolicy()
        policy.setPolicies(
            arrayOf<CompletionPolicy>(
                TimeoutTerminationPolicy(1000),
                SimpleCompletionPolicy(10)
            )
        )
        return policy
    }


}