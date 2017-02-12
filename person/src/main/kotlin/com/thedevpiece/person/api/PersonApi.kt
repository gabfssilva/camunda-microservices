package com.thedevpiece.person.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter
import javax.persistence.Entity

/**
 * @author Gabriel Francisco <peo_gfsilva@uolinc.com>
 */
@Entity
data class Person(val id: Long = 0,
                  val name: String = "",
                  val age: Int = -1,
                  val occupation: String = "")

@RepositoryRestResource
interface PersonRestRepository : PagingAndSortingRepository<Person, Long>

@SpringBootApplication
open class Application

@Configuration
open class WebConfiguration : RepositoryRestConfigurerAdapter() {
    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.exposeIdsFor(Person::class.java)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}