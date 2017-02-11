package com.thedevpiece.payment.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import javax.persistence.Entity

/**
 * @author Gabriel Francisco <peo_gfsilva@uolinc.com>
 */
@Entity
data class Payment(val id: Long = 0,
                   val value: Int = 0,
                   val person: Int = 0,
                   val paymentMethod: String = "",
                   val status: String = "")

@RepositoryRestResource
interface PaymentRestRepository : PagingAndSortingRepository<Payment, Long>

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}