package it.mirrorshot.imdb_etl.entities

import it.mirrorshot.imdb_etl.*
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
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
import java.io.Serializable
import java.nio.file.Path

@Entity
@IdClass(value = AkaID::class)
@Table(name = "aka")
data class Aka(
    @Id
    val title: String,
    @Id
    val ordering: Int,
    var localized: String,
    val region: String?,
    val language: String?,
    val types: String?,
    val attributes: String?,
    val isOriginalTitle: Boolean?
)

data class AkaID(
    val title: String,
    val ordering: Int
) : Serializable


@Component
class AkaMapper : LineMapper<Aka> {
    override fun mapLine(text: String, index: Int): Aka =
        text.split("\t")
            .let { values ->
                Aka(
                    values[0].clear()!!,
                    values[1].clear()!!.toInt(),
                    values[2].clear()!!,
                    values[3].clear(),
                    values[4].clear(),
                    values[5].clear(),
                    values[6].clear(),
                    values[7].clear()?.asBooleanOrNull()
                )
            }
}

@Configuration
@ConditionalOnProperty(prefix = "imdb.loading.aka", name = ["enabled"], havingValue = "true")
class AkaConfiguration {
    @Bean
    fun akaReader(
        mapper: AkaMapper,
        @Value(value = "\${imdb.loading.aka.location:data/title.akas.tsv}") path: Path
    ): ItemReader<Aka> = commonReader(
        "akaReader",
        FileSystemResource(path),
        mapper,
        "titleId",
        "ordering",
        "title",
        "region",
        "language",
        "types",
        "attributes",
        "isOriginalTitle"
    )

    @Bean
    fun loadAkas(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        reader: ItemReader<Aka>,
        repository: AkaRepository
    ): Step = commonStep(
        "loadAkas",
        jobRepository,
        transactionManager,
        reader,
        repository
    )
}

@Repository
interface AkaRepository : CrudRepository<Aka, AkaID>
