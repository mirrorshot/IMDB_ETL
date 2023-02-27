package it.mirrorshot.imdb_etl.entities

import it.mirrorshot.imdb_etl.clear
import it.mirrorshot.imdb_etl.commonReader
import it.mirrorshot.imdb_etl.commonStep
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
@IdClass(value = EpisodeId::class)
@Table(name = "episode")
data class Episode(
    @Id
    val title: String,
    @Id
    val episode: String,
    val seasonNumber: Int?,
    val episodeNumber: Int?
)

data class EpisodeId(
    val title: String,
    val episode: String
) : Serializable

@Component
class EpisodeMapper : LineMapper<Episode> {
    override fun mapLine(text: String, index: Int): Episode = text.split("\t")
        .let { values ->
            Episode(
                values[0].clear()!!,
                values[1].clear()!!,
                values[2].clear()?.toInt(),
                values[3].clear()?.toInt()
            )
        }
}

@Configuration
@ConditionalOnProperty(prefix = "imdb.loading.episodes", name = ["enabled"], havingValue = "true")
class EpisodeConfiguration {
    @Bean
    fun episodeReader(
        mapper: EpisodeMapper,
        @Value(value = "\${imdb.loading.episodes.location:data/title.episodes.tsv}") path: Path
    ): ItemReader<Episode> = commonReader(
        "episodeReader",
        FileSystemResource(path),
        mapper,
        "tconst",
        "parentTconst",
        "seasonNumber",
        "episodeNumber"
    )

    @Bean
    fun loadEpisodes(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        reader: ItemReader<Episode>,
        repository: EpisodeRepository
    ): Step = commonStep(
        "loadEpisodes",
        jobRepository,
        transactionManager,
        reader,
        repository
    )
}

@Repository
interface EpisodeRepository : CrudRepository<Episode, EpisodeId>
