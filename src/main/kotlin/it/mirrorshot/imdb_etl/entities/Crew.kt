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
@Table(name = "crew")
data class Crew(
    @Id
    val title: String,
    val directors: String?,
    val writers: String?
)


@Component
class CrewMapper : LineMapper<Crew> {
    override fun mapLine(text: String, index: Int): Crew =
        text.split("\t")
            .let { values ->
                Crew(
                    values[0].clear()!!,
                    values[1].clear(),
                    values[2].clear()
                )
            }
}

@Configuration
@ConditionalOnProperty(prefix = "imdb.loading.crew", name = ["enabled"], havingValue = "true")
class CrewConfiguration {
    @Bean
    fun crewReader(
        mapper: CrewMapper,
        @Value(value = "\${imdb.loading.crew.location:data/title.crew.tsv}") path: Path
    ): ItemReader<Crew> = commonReader(
        "crewReader",
        FileSystemResource(path),
        mapper,
        "tconst", "directors", "writers"
    )

    @Bean
    fun loadCrews(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        reader: ItemReader<Crew>,
        repository: CrewRepository
    ): Step = commonStep(
        "loadCrews",
        jobRepository,
        transactionManager,
        reader,
        repository
    )
}

@Repository
interface CrewRepository : CrudRepository<Crew, String>
