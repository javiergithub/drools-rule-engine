package com.yonatankarp.drools.service

import com.yonatankarp.drools.model.DeliveredData
import com.yonatankarp.drools.model.mysql.MysqlData
import com.yonatankarp.drools.model.mysql2.Mysql2Data


interface DataProcessor {
    abstract fun combineData(mysql2DataList: List<Mysql2Data>, mysqlDataList: List<MysqlData>): List<DeliveredData>
}
