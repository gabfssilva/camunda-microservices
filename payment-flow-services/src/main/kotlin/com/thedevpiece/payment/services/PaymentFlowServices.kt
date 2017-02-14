package com.thedevpiece.payment.services

import com.beust.klaxon.*
import com.fasterxml.jackson.annotation.JsonAnySetter
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.net.URI
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.omg.CORBA.TIMEOUT



/**
 * @author Gabriel Francisco <peo_gfsilva@uolinc.com>
 */
data class Person(val id: Long = 0,
                  val name: String = "",
                  val age: Int = -1,
                  val occupation: String = "",
                  val status: String = "")

data class Payment(val id: Long = 0,
                   val value: Int = 0,
                   val person: Long = 0,
                   val paymentMethod: String = "",
                   val status: String = "")

data class Context(val properties: Map<String, String> = emptyMap())

@RestController
class PaymentRestRepository(val restTemplate: RestTemplate) {

    @PostMapping("/getNewPerson")
    fun getPerson() = """
            {
                "properties": {
                    "personName": "Gabriel Francisco",
                    "personAge": 24,
                    "personOccupation": "Software Engineer"
                }
            }"""

    @PostMapping("/savePerson")
    fun savePerson(@RequestBody context: Context): Context {
        val person = Person(
                name = context.properties["personName"]!!,
                age = context.properties["personAge"]!!.toInt(),
                occupation = context.properties["personOccupation"]!!
        )

        val personUri = "http://localhost:8081/person-api/rs/persons/".asUri()
        val location = restTemplate.postForEntity(personUri, person, String::class.java).headers["Location"]!![0]

        return Context(properties = hashMapOf("personUri" to location))
    }

    @PostMapping("/updatePersonActive")
    fun updatePersonActive(@RequestBody context: Context): Context {
        restTemplate.exchange(Person(status = "ACTIVE").asPatch(context.properties["personUri"]!!), String::class.java)
        return Context(properties = hashMapOf("personStatus" to "ACTIVE"))
    }

    @PostMapping("/updatePersonInactive")
    fun updatePersonInactive(@RequestBody context: Context): Context {
        restTemplate.exchange(Person(status = "INACTIVE").asPatch(context.properties["personUri"]!!), String::class.java)
        return Context(properties = hashMapOf("personStatus" to "INACTIVE"))
    }

    @PostMapping("/pay")
    fun pay(@RequestBody context: Context): Context {
        val person =
                restTemplate
                        .getForEntity(context.properties["personUri"]!!, String::class.java)
                        .body
                        .asJson()
                        .long("id")!!

        val value: Int = context.properties["value"]!!.toInt()
        val paymentMethod: String = context.properties["paymentMethod"]!!

        val payment = Payment(value = value, paymentMethod = paymentMethod, person = person)
        val paymentUri = "http://localhost:8083/payment-api/rs/payments/".asUri()

        val paymentLocation: String =
                restTemplate
                        .postForEntity(paymentUri, payment, String::class.java)
                        .headers["Location"]!![0]

        val status = restTemplate
                .postForEntity("http://localhost:8082/payment-gateway/rs/payment", payment, String::class.java)
                .body.asJson()
                .string("status")!!

        val paymentId = restTemplate
                .exchange(Payment(status = status).asPatch(paymentLocation), String::class.java)
                .body
                .asJson()
                .int("id")!!

        return Context(properties = hashMapOf("payment" to paymentId.toString(), "status" to status))
    }
}

@SpringBootApplication
open class Application {
    @Bean open fun requestFactory() = HttpComponentsClientHttpRequestFactory()
    @Bean open fun restTemplate(): RestTemplate = RestTemplate(requestFactory())
}

fun String.asUri() = URI(this)
fun String.asJson() = Parser().parse(StringBuilder(this)) as JsonObject
fun Any.asPatch(uri: String) = RequestEntity(this, HttpMethod.PATCH, uri.asUri())

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}