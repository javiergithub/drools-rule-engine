package com.yonatankarp.drools.model

import java.time.LocalDateTime
import java.util.Date

class Trade() {
    fun toTrades(): Trades {
        val tr : Trades = Trades()
        tr.id = this.id
        tr.processDate = this.processDate
        return tr
    }

    var processDate: LocalDateTime? = null
    var id : Long = 0
}