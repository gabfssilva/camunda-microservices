package com.thedevpiece.gateway.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Min

/**
 * @author Gabriel Francisco <peo_gfsilva@uolinc.com>
 */
data class PaymentMock(@Min(1) val value: Int = 0, val status: String = "")

@RestController
@RequestMapping("/payment")
class PaymentMockEndpoint {
    @PostMapping
    fun pay(@RequestBody body: PaymentMock): PaymentMock = when {
        body.value == 3000 -> PaymentMock(body.value, "PAID")
        else -> PaymentMock(body.value, "NOT_PAID")
    }
}

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}