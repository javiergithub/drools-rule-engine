
package com.yonatankarp.drools

import com.yonatankarp.drools.config.BatchConfig
import com.yonatankarp.drools.config.BatchConfig.MyEventListener
import com.yonatankarp.drools.config.TradeJobConfig
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.duckdb.DuckDBConnection
import org.duckdb.DuckDBResultSet
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.FileSystemResource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement
import java.text.SimpleDateFormat


@Component
//@ConditionalOnProperty(prefix = "schedule", name = ["active"], havingValue = "true")
class SimpleJobScheduler(
    val jobLauncher: JobLauncher,
    val simpleJobConfig: BatchConfig,
    val tradeJobConfig : TradeJobConfig,
    val myEventListener: MyEventListener,
    @Qualifier("tradeJob") @Autowired  val tradeJob : Job

) {
    private val logger: Log = LogFactory.getLog(SimpleJobScheduler::class.java)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    private val dbRuta = "/home/javier/win-d/var/timingest/db/"
    private val dbName = "tim_ingest"
    private val tradeTable: String = "trade"
    private val dbPrefix: String = "duckdb_"
    val tradeEsquema: String =
        "tradeId LONG, processDate DATETIME, PRIMARY KEY (tradeId)"
    private val tradeSql  = "select  * from duckdb_trade  "
    private val tradeResource = FileSystemResource("/home/javier/win-d/var/timingest/data/trade.csv");
    @Scheduled(initialDelay = 10000, fixedDelay = 30000)
    fun runJob(){
        val conn : DuckDBConnection = (DriverManager.getConnection("jdbc:duckdb:" + dbRuta.plus(dbName)) as DuckDBConnection);
        dropTable(tradeTable, conn)
        createTable(tradeTable, tradeEsquema, conn);
//        loadDuckdbTableTrade(conn)
//        queryDuckdbTableTrade(tradeSql, conn)
        val jobConf = hashMapOf<String, JobParameter<*>>()
        val jobParametersBuilder: JobParametersBuilder = JobParametersBuilder()
        jobParametersBuilder.addLong("time",System.currentTimeMillis())
     //   jobConf["time"] = JobParameter<Time>(dateFormat.format(System.currentTimeMillis()))
        val jobParameters = jobParametersBuilder.toJobParameters()
        val jobParametersBuilder2: JobParametersBuilder = JobParametersBuilder()
        jobParametersBuilder2.addString("tradeFile", "/home/javier/win-d/var/timingest/data/cut-0-20220516.Trades-short-1.csv")
        jobParametersBuilder2.addLong("time",System.currentTimeMillis())
        val jobParameters2 = jobParametersBuilder2.toJobParameters()

        try{
            jobLauncher.run(simpleJobConfig.dataProcessingJob(myEventListener), jobParameters)
            jobLauncher.run(tradeJob, jobParameters2)
        } catch(e: JobExecutionAlreadyRunningException){
            logger.error(e.localizedMessage)
        } catch(e: JobParametersInvalidException){
            logger.error(e.localizedMessage)
        }
        conn.close()
    }
    @Throws(SQLException::class)
    fun dropTable(tabla: String?, conn : DuckDBConnection) {
        val stmt: Statement = conn.createStatement()
        stmt.execute("DROP TABLE IF EXISTS " + dbPrefix.plus(tabla))
    }

    @Throws(SQLException::class)
    fun createTable(tabla: String?, esquema: String?, conn : DuckDBConnection) {
        val stmt: Statement = conn.createStatement()
        stmt.execute("CREATE TABLE " + dbPrefix.plus(tabla).plus(" ").plus("(").plus(esquema).plus(")"))
    }
    @Throws(SQLException::class)
    fun loadDuckdbTableTrade(conn : DuckDBConnection) {
//        val tradeL: Page<Solicitud> = solicitudRepositorio.findAll(PageRequest.of(0, 2000000)) as Page<Solicitud>
        conn.createAppender(DuckDBConnection.DEFAULT_SCHEMA, dbPrefix.plus(tradeTable)).use { appender ->
            for (i in 1..4) {
                appender.beginRow()
                appender.append(i)
                appender.endRow()
            }
        }
    }

    @Throws(SQLException::class)
    fun queryDuckdbTableTrade( sql : String, conn : DuckDBConnection) {
        val stmt = conn.prepareStatement(sql)
        val rs = stmt.executeQuery() as DuckDBResultSet
        while (rs.next()) {
            var idx : Int = 1
            val tradeId : Long = rs.getLong(idx)
            idx = idx + 1
        }
    }

}