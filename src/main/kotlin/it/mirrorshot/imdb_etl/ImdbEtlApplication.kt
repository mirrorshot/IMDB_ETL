package it.mirrorshot.imdb_etl

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import kotlin.system.exitProcess

@SpringBootApplication
class ImdbEtlApplication {

}

fun main(args: Array<String>) {
    exitProcess(SpringApplication.exit(SpringApplication.run(ImdbEtlApplication::class.java, *args)))
}
