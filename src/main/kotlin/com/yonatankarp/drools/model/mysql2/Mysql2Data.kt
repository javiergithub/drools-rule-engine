package com.yonatankarp.drools.model.mysql2

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "mysql_2_data")
data class Mysql2Data(@Id @GeneratedValue val id:Long?=-1, var name : String="")
