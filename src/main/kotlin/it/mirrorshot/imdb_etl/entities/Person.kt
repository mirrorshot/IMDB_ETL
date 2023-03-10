package it.mirrorshot.imdb_etl.entities

import it.mirrorshot.imdb_etl.clear
import it.mirrorshot.imdb_etl.commonReader
import it.mirrorshot.imdb_etl.commonStep
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.batch.core.Step
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.file.LineMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.PlatformTransactionManager
import java.nio.file.Path

@Entity
@Table(name = "person")
data class Person(
    @Id
    val person: String,
    val name: String,
    val birthYear: Int?,
    val deathYear: Int?,
    val profession: String?,
    val knownFor: String?
)

@Component
class PersonMapper : LineMapper<Person> {
    override fun mapLine(text: String, index: Int): Person =
        text.split("\t")
            .let { values ->
                Person(
                    values[0].clear()!!,
                    values[1].clear()!!,
                    values[2].clear()?.toInt(),
                    values[3].clear()?.toInt(),
                    values[4].clear(),
                    values[5].clear()
                )
            }
}

@Configuration
@ConditionalOnProperty(prefix = "imdb.loading.people", name = ["enabled"], havingValue = "true")
class PersonConfiguration {
    @Bean
    fun personReader(
        mapper: PersonMapper,
        @Value(value = "\${imdb.loading.people.location:data/name.basics.tsv}") path: Path
    ): ItemReader<Person> = commonReader(
        "personReader",
        FileSystemResource(path),
        mapper,
        "nconst",
        "primaryName",
        "birthYear",
        "deathYear",
        "primaryProfession",
        "knownForTitles"
    )

    @Bean
    fun loadPeople(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        reader: ItemReader<Person>,
        repository: PersonRepository
    ): Step = commonStep(
        "loadPeople",
        jobRepository,
        transactionManager,
        reader,
        repository,
        chunkSize = 10000
    )
}

@Repository
interface PersonRepository : CrudRepository<Person, String>
