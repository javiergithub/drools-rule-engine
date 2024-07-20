package com.yonatankarp.drools.processor

import com.yonatankarp.drools.config.DroolsBeanFactory
import com.yonatankarp.drools.model.Trade
import com.yonatankarp.drools.model.Trades
import org.kie.api.command.Command
import org.kie.api.runtime.ExecutionResults
import org.kie.internal.command.CommandFactory
import org.springframework.batch.item.ItemProcessor
import java.util.stream.Collectors


class TradeDuckdbItemProcessor() : ItemProcessor<List<Trade>,List<Trade>> {
    private val kieSession = DroolsBeanFactory().getStatelessKieSession("trades_rules.drl.xls")
    override fun process(item: List<Trade>): List<Trade> {
        val arrayList = item.stream().map { tr: Trade? ->
            val trs : Trades? = tr?.toTrades()
            trs
        }.collect(Collectors.toList())
        val results: ExecutionResults?
        try {
            val cmds: List<Command<*>> = mutableListOf<Command<*>>(
                CommandFactory.newInsertElements(arrayList, "tradesL", true, null),
                CommandFactory.newFireAllRules()
            )

            results =kieSession.execute(CommandFactory.newBatchExecution(cmds))
        } finally {
            //kieSession.dispose();
        }
         return item
    }

}