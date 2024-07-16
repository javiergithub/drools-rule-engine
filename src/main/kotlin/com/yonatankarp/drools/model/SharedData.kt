package com.yonatankarp.drools.model

import com.yonatankarp.drools.dao.mysql.MysqlDataRepository
import com.yonatankarp.drools.dao.mysql2.Mysql2DataRepository
import com.yonatankarp.drools.model.mysql.MysqlData
import com.yonatankarp.drools.model.mysql2.Mysql2Data
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SharedData(@Autowired val mysqlDataRepository: MysqlDataRepository,
                 @Autowired val mysql2DataRepository: Mysql2DataRepository
) {

    private val mysql2DataStore: MutableList<Mysql2Data> = mutableListOf()
    private val mysqlDataStore: MutableList<MysqlData> = mutableListOf()
    // Methods to add and retrieve data from the stores
    fun addMysql2Data(mysql2Data: Mysql2Data) {
        mysql2DataRepository.save(mysql2Data)
    }
    fun getMysql2Data() : List<Mysql2Data> {
        return mysql2DataRepository.findAll().toList()
    }
    fun addMysqlData(mysqlData: MysqlData) {
        mysqlDataRepository.save(mysqlData)
    }
    fun getMysqlData() : List<MysqlData> {
        return mysqlDataRepository.findAll().toList()
    }
}