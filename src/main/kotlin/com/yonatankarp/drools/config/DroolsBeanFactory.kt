package com.yonatankarp.drools.config

import org.drools.decisiontable.DecisionTableProviderImpl
import org.kie.api.KieServices
import org.kie.api.builder.KieFileSystem
import org.kie.api.io.Resource
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.StatelessKieSession
import org.kie.internal.builder.DecisionTableInputType
import org.kie.internal.builder.KnowledgeBuilderFactory
import org.kie.internal.io.ResourceFactory


class DroolsBeanFactory {
    private val kieServices: KieServices = KieServices.Factory.get()

    private val kieFileSystem: KieFileSystem
        get() {
            val kieFileSystem = kieServices.newKieFileSystem()
            val rules: List<String> = mutableListOf(
                "rules/trades_rules.drl.xls"
            )
            for (rule in rules) {
                kieFileSystem.write(ResourceFactory.newClassPathResource(rule))
            }
            return kieFileSystem
        }

    private val kieRepository: Unit
        get() {
            val kieRepository = kieServices.repository
            kieRepository.addKieModule { kieRepository.defaultReleaseId }
        }

    val kieSession: KieSession
        get() {
            val kb = kieServices.newKieBuilder(kieFileSystem)
            kb.buildAll()

            val kieRepository = kieServices.repository
            val krDefaultReleaseId = kieRepository.defaultReleaseId
            val kieContainer = kieServices.newKieContainer(krDefaultReleaseId)

            return kieContainer.newKieSession()
        }

    fun getKieSession(dt: Resource?): KieSession {
        val kieFileSystem = kieServices.newKieFileSystem()
            .write(dt)

        val kieBuilder = kieServices.newKieBuilder(kieFileSystem)
            .buildAll()

        val kieRepository = kieServices.repository

        val krDefaultReleaseId = kieRepository.defaultReleaseId

        val kieContainer = kieServices.newKieContainer(krDefaultReleaseId)

        val ksession = kieContainer.newKieSession()

        return ksession
    }

    /*
     * Can be used for debugging
     * Input excelFile example: com/baeldung/drools/rules/Discount.drl.xls
     */
    fun getDrlFromExcel(excelFile: String?): String {
        val configuration = KnowledgeBuilderFactory.newDecisionTableConfiguration()
        configuration.inputType = DecisionTableInputType.XLS

        val dt = ResourceFactory.newClassPathResource(excelFile, javaClass)

        val decisionTableProvider = DecisionTableProviderImpl()

        val drl = decisionTableProvider.loadFromResource(dt, null)

        return drl
    }

    private fun getKieRepository() {
        val kieRepository = kieServices.repository
        kieRepository.addKieModule { kieRepository.defaultReleaseId }
    }

    fun getStatelessKieSession(drlRules : String): StatelessKieSession {
        getKieRepository()
        val kieFileSystem = kieServices.newKieFileSystem()

        kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_PATH.plus(drlRules)))


        val kb = kieServices.newKieBuilder(kieFileSystem)
        kb.buildAll()
        val kieModule = kb.kieModule

        val kContainer = kieServices.newKieContainer(kieModule.releaseId)

        return kContainer.newStatelessKieSession()
    }

    companion object {
        private const val RULES_PATH = "rules/"
    }
}
