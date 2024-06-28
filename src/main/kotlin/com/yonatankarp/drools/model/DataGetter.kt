package com.yonatankarp.drools.model

import com.yonatankarp.drools.dao.mysql.MysqlDataRepository
import com.yonatankarp.drools.dao.mysql2.Mysql2DataRepository
import com.yonatankarp.drools.model.mysql.MysqlData
import com.yonatankarp.drools.model.mysql2.Mysql2Data
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DataGetter(@Autowired val mysqlDataRepository: MysqlDataRepository,
                 @Autowired  val mysql2DataRepository: Mysql2DataRepository
) {
    fun getDataFromMysql2DB(): Mysql2Data {
        var mysql2Data: Mysql2Data? = null
        mysql2Data = mysql2DataRepository.findFirstByName(name = "Mysql2")
        return mysql2Data!!
    }

    fun getDataFromMysqlDB(): MysqlData {
        var mysqlData: MysqlData? = null
        mysqlData = mysqlDataRepository.findFirstByName(name = "Mysql")
        return mysqlData!!
    }
}