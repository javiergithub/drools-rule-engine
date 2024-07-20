package com.yonatankarp.drools.model

import jakarta.persistence.Column
import java.io.Serializable
import java.math.BigDecimal
import java.util.*


class Country  : Serializable {
    private var seqNo: Long? = null // int(11) NOT NULL AUTO_INCREMENT,
    private var shortDesc: String? = null // varchar(50) NOT NULL,

    @Column(name = "longDesc")
    private var longDesc: String? = null // varchar(50) NOT NULL,

    @Column(name = "creationDt")
    private var creationDt: Date? = null // datetime NOT NULL COMMENT 'Date created\n',

    @Column(name = "createdBySeqNo")
    private var createdBySeqNo: Long? = null // int(11) NOT NULL COMMENT 'users.seqNo - User who created this\n',

    @Column(name = "lastModDt")
    private var lastModDt: Date? = null // datetime DEFAULT NULL COMMENT 'users.seqNo - User who created this\n',

    @Column(name = "lastModBySeqNo")
    private var lastModBySeqNo: Long? = null // int(11) DEFAULT NULL COMMENT 'users.seqNo - Last person modifying\n',

    @Column(name = "countryGroupsSeqNo")
    private var countryGroupsSeqNo: Long? = null // int(11) DEFAULT NULL,

    @Column(name = "vatRate")
    private var vatRate: BigDecimal? = null // decimal(7,4) DEFAULT NULL,

    @Column(name = "usesVAT", columnDefinition = "tinyint(1) default '0'")
    private var usesVAT: Boolean? = null // tinyint(1) DEFAULT 0,

    @Column(name = "regulatoryGroupSeqNo")
    private var regulatoryGroupSeqNo: Long? = null // int(11) DEFAULT NULL,

    @Column(name = "clientPaysVAT", columnDefinition = "tinyint(1) default '0'")
    private var clientPaysVAT: Boolean? = null // tinyint(1) DEFAULT 0,

    @Column(name = "brokerPaysVAT", columnDefinition = "tinyint(1) default '1'")
    private var brokerPaysVAT: Boolean? = null // tinyint(1) DEFAULT 1,

    fun Country(seqNo: Long?, shortDesc: String?) {
        this.seqNo = seqNo
        this.shortDesc = shortDesc
    }

    fun Country(shortDesc: String?, longDesc: String?, creationDt: Date?, createdBySeqNo: Long?) {
        this.shortDesc = shortDesc
        this.longDesc = longDesc
        this.creationDt = creationDt
        this.createdBySeqNo = createdBySeqNo
    }

/*    fun Country(country: Country) {
        this.seqNo = country.getSeqNo()
        this.shortDesc = country.getShortDesc()
        this.longDesc = country.getLongDesc()
        try {
            this.creationDt = if (country.getCreationDt() != null) DateUtils.parseDate(
                country.getCreationDt(),
                com.castine.timload.map339.util.DateUtils.DATE_TIME_PATTERN
            ) else null
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        this.createdBySeqNo =
            if (country.getCreatedBySeqNo() != null) java.lang.Long.valueOf(country.getCreatedBySeqNo()) else null
        try {
            this.lastModDt = if (country.getLastModDt() != null) DateUtils.parseDate(
                country.getLastModDt(),
                com.castine.timload.map339.util.DateUtils.DATE_TIME_PATTERN
            ) else null
        } catch (e1: ParseException) {
            e1.printStackTrace()
        }
        this.lastModBySeqNo =
            if (country.getLastModBySeqNo() != null) java.lang.Long.valueOf(country.getLastModBySeqNo()) else null
        this.countryGroupsSeqNo =
            if (country.getCountryGroupsSeqNo() != null) java.lang.Long.valueOf(country.getCountryGroupsSeqNo()) else null
        this.vatRate = if (country.getVatRate() != null) BigDecimal(country.getVatRate()) else null
        this.usesVAT =
            if (country.getUsesVAT() != null) (if (country.getUsesVAT().compareTo("1") === 0) true else false) else null
        this.regulatoryGroupSeqNo =
            if (country.getRegulatoryGroupSeqNo() != null) java.lang.Long.valueOf(country.getRegulatoryGroupSeqNo()) else null
        this.clientPaysVAT = if (country.getClientPaysVAT() != null) (if (country.getClientPaysVAT()
                .compareTo("1") === 0
        ) true else false) else null
        this.brokerPaysVAT = if (country.getBrokerPaysVAT() != null) (if (country.getBrokerPaysVAT()
                .compareTo("1") === 0
        ) true else false) else null
    }*/

    /*fun toCountryMap(): Country {
        val country = Country()
        country.setSeqNo(this.getSeqNo())
        country.setShortDesc(this.getShortDesc())
        country.setLongDesc(this.getLongDesc())
        try {
            country.setCreationDt(
                if (this.getCreationDt() != null) parseDateDT(
                    this.getCreationDt().toString(),
                    com.castine.timload.map339.util.DateUtils.DATE_TIME_PATTERN
                ) else null
            )
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        if (country.getCreationDt() == null) {
            try {
                country.setCreationDt(
                    formatDateTimeAsString(
                        Date(),
                        com.castine.timload.map339.util.DateUtils.DATE_TIME_PATTERN
                    )
                )
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
        country.setCreatedBySeqNo(if (this.getCreatedBySeqNo() != null) this.getCreatedBySeqNo().toString() else null)
        try {
            country.setLastModDt(
                if (this.getLastModDt() != null) parseDateDT(
                    this.getLastModDt().toString(),
                    com.castine.timload.map339.util.DateUtils.DATE_TIME_PATTERN
                ) else null
            )
        } catch (e1: ParseException) {
            e1.printStackTrace()
        }
        country.setLastModBySeqNo(
            if (this.getLastModBySeqNo() != null) java.lang.Long.valueOf(this.getLastModBySeqNo()).toString() else null
        )
        country.setCountryGroupsSeqNo(
            if (this.getCountryGroupsSeqNo() != null) this.getCountryGroupsSeqNo().toString() else null
        )
        country.setVatRate(if (this.getVatRate() != null) this.getVatRate().toString() else null)
        country.setUsesVAT(if (this.getUsesVAT() != null) (if (this.getUsesVAT()) "1" else "0") else null)
        country.setRegulatoryGroupSeqNo(
            if (this.getRegulatoryGroupSeqNo() != null) this.getRegulatoryGroupSeqNo().toString() else null
        )
        country.setClientPaysVAT(if (this.getClientPaysVAT() != null) (if (this.getClientPaysVAT()) "1" else "0") else null)
        country.setBrokerPaysVAT(if (this.getBrokerPaysVAT() != null) (if (this.getBrokerPaysVAT()) "1" else "0") else null)
        return country
    }*/

}