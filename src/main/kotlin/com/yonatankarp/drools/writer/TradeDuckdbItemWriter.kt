package com.yonatankarp.drools.writer

import com.yonatankarp.drools.model.Trade
import org.duckdb.DuckDBConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import java.sql.Connection
import java.sql.DriverManager


class TradeDuckdbItemWriter : ItemWriter<List<com.yonatankarp.drools.model.Trade>> {

 /*   @Throws(Exception::class)
    fun write(items: List<T>) {
        LOG.trace("Console item writer starts")
        for (item in items) {
            LOG.info(ToStringBuilder.reflectionToString(item))
        }
        LOG.trace("Console item writer ends")
    }*/
    private val dbRuta = "/home/javier/win-d/var/timingest/db/"
    private val dbName = "tim_ingest"
    private val tradeTable: String = "trade"
    private val dbPrefix: String = "duckdb_"

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(TradeDuckdbItemWriter::class.java)
    }

    override fun write(chunk: Chunk<out List<Trade>>) {
        LOG.trace("Console item writer starts")
        val conn : DuckDBConnection = (DriverManager.getConnection("jdbc:duckdb:" + dbRuta.plus(dbName)) as DuckDBConnection);
        conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

        // We now set the autocommit to "false". This means any SQL statements
        // will implicitly start a new transaction.
        conn.setAutoCommit(false);

        conn.createAppender(DuckDBConnection.DEFAULT_SCHEMA, dbPrefix.plus(tradeTable)).use { appender ->
            for (item in chunk) {
                for (trade in item) {
                    appender.beginRow()
                    appender.append(trade.id)
                    appender.appendLocalDateTime(trade.processDate)
                    appender.endRow()
                }
            }
        }
        conn.commit()
        conn.close()
        LOG.trace("Console item writer ends")
    }
}