package com.yonatankarp.drools.dao.mysql2

import com.yonatankarp.drools.model.mysql2.Mysql2Data
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface Mysql2DataRepository: CrudRepository<Mysql2Data, Long> {
    fun findFirstByName(name: String): Mysql2Data
}