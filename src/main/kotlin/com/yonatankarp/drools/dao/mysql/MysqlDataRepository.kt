package com.yonatankarp.drools.dao.mysql

import com.yonatankarp.drools.model.mysql.MysqlData
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MysqlDataRepository: CrudRepository<MysqlData, Long> {
    fun findFirstByName(name: String): MysqlData
}