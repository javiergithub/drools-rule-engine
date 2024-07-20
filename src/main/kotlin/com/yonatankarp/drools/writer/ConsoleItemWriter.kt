package com.yonatankarp.drools.writer

import org.apache.commons.lang3.builder.ToStringBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter


class ConsoleItemWriter<T> : ItemWriter<T> {

 /*   @Throws(Exception::class)
    fun write(items: List<T>) {
        LOG.trace("Console item writer starts")
        for (item in items) {
            LOG.info(ToStringBuilder.reflectionToString(item))
        }
        LOG.trace("Console item writer ends")
    }*/

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(ConsoleItemWriter::class.java)
    }

    override fun write(chunk: Chunk<out T>) {
        LOG.trace("Console item writer starts")
        for (item in chunk) {
            LOG.info(ToStringBuilder.reflectionToString(item))
        }
        LOG.trace("Console item writer ends")
    }
}