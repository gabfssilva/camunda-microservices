package com.thedevpiece.payment.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


/**
 * @author Gabriel Francisco <peo_gfsilva@uolinc.com>
 */
@Entity
data class Payment(@Id @GeneratedValue val id: Long = 0,
                   val value: Int = 0,
                   val person: Int = 0,
                   val paymentMethod: String = "",
                   val status: String = "CREATED")

@RepositoryRestResource
interface PaymentRestRepository : PagingAndSortingRepository<Payment, Long>

@SpringBootApplication
open class Application

@Configuration
open class WebConfiguration : RepositoryRestConfigurerAdapter() {
    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.exposeIdsFor(Payment::class.java)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}