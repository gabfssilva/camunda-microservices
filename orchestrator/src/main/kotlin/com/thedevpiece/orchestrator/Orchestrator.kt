package com.thedevpiece.orchestrator

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.Expression
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.camunda.bpm.spring.boot.starter.configuration.CamundaDeploymentConfiguration
import org.camunda.bpm.spring.boot.starter.configuration.impl.DefaultDeploymentConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.web.client.RestTemplate
import java.net.URI

/**
 * @author Gabriel Francisco <peo_gfsilva@uolinc.com>
 */
data class Context(val properties: Map<String, String> = emptyMap())

object SingletonHolder {
    val restTemplate: RestTemplate by lazy {
        RestTemplate()
    }
}

class GenericHttpDelegate : JavaDelegate {
    val restTemplate = SingletonHolder.restTemplate

    override fun execute(execution: DelegateExecution?) {
        val context = Context(properties = execution!!.variables.entries.associateBy({ it.key }, { it.value.toString() }))
        val url: String = execution.variables["url"] as String
        val result = restTemplate.postForEntity(URI(url), context, Context::class.java).body
        result.properties.forEach { it -> execution.setVariable(it.key, it.value) }
    }
}

@SpringBootApplication
open class Application {
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}