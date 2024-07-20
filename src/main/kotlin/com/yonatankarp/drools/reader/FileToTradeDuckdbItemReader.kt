package com.yonatankarp.drools.reader

import com.yonatankarp.drools.model.Trade
import org.duckdb.DuckDBConnection
import org.duckdb.DuckDBResultSet
import org.springframework.batch.core.ItemReadListener
import org.springframework.batch.item.ItemReader
import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class FileToTradeDuckdbItemReader(val file : String, val dbRuta : String, val dbName : String, val tablePrefix : String, val tableName : String,
                                  val pageSize : Int = 100000) : ItemReader<List<Trade>>,
    ItemReadListener<List<Trade>> {
    private var conn : DuckDBConnection =   (DriverManager.getConnection("jdbc:duckdb:" + dbRuta.plus(dbName)) as DuckDBConnection);
    private var stmt = conn.createStatement();
    private var page = 0
    private val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
    init {
        println("First initializer block that prints $file")
        stmt.execute("DROP TABLE IF EXISTS ${tablePrefix.plus(tableName)}")
        val query = java.lang.String.format("CREATE TABLE %s AS SELECT * FROM read_csv('%s')", tablePrefix.plus(tableName), file)
        stmt.execute(query)
    }
    override fun read(): List<Trade>? {
        var tradeList : MutableList<Trade> = java.util.ArrayList<Trade>()
        val tradePaginateQuery = java.lang.String.format(" SELECT * FROM %s LIMIT %s OFFSET %s", tablePrefix.plus(tableName), pageSize, page)
        val rs :  DuckDBResultSet = stmt.executeQuery(tradePaginateQuery)  as DuckDBResultSet
        var index : Long = 0
        while (rs.next()) {
            val trade : Trade= Trade()
            var idx : Long = 1
            index += 1
            val tradeId : Long = index + page
            trade.id = tradeId
            trade.processDate = LocalDateTime.parse(rs.getString(idx.toInt()),pattern)
            idx = idx + 1
            tradeList.add(trade)
        }
        page += 100000
        if (tradeList.size == 0) {
            return null;
        }
         return tradeList
    }

    override fun beforeRead() {
        super.beforeRead()
        conn = (DriverManager.getConnection("jdbc:duckdb:" + dbRuta.plus(dbName)) as DuckDBConnection);
        stmt = conn.createStatement()

    }

    override fun afterRead(item: List<Trade>) {
        super.afterRead(item)
        stmt.close()
        conn.close()
    }
}