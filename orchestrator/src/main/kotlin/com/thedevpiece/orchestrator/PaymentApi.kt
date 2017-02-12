package com.thedevpiece.orchestrator

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.Expression
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.client.RestTemplate
import java.net.URI

/**
 * @author Gabriel Francisco <peo_gfsilva@uolinc.com>
 */
data class Context(val properties: Map<String, String>)

object SingletonHolder {
    val restTemplate: RestTemplate by lazy {
        RestTemplate()
    }
}

class GenericHttpDelegate : JavaDelegate {
    val restTemplate = SingletonHolder.restTemplate
    var url: Expression? = null

    override fun execute(execution: DelegateExecution?) {
        val context = Context(properties = execution!!.variables.entries.associateBy({ it.key }, { it.value.toString() }))
        val result = restTemplate.postForEntity(URI(url!!.getValue(execution).toString()), context, Context::class.java).body
        result.properties.forEach { it -> execution.setVariable(it.key, it.value) }
    }
}

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}