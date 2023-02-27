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
@Table(name = "rating")
data class Rating(
    @Id
    val title: String,
    val averageRating: Double,
    val votes: Int
)

@Component
class RatingMapper : LineMapper<Rating> {
    override fun mapLine(text: String, index: Int): Rating =
        text.split("\t")
            .let { values ->
                Rating(
                    values[0].clear()!!,
                    values[1].clear()!!.toDouble(),
                    values[2].clear()!!.toInt()
                )
            }
}

@Configuration
@ConditionalOnProperty(prefix = "imdb.loading.ratings", name = ["enabled"], havingValue = "true")
class RatingConfiguration {
    @Bean
    fun ratingReader(
        mapper: RatingMapper,
        @Value(value = "\${imdb.loading.ratings.location:data/title.ratings.tsv}") path: Path
    ): ItemReader<Rating> = commonReader(
        "ratingReader",
        FileSystemResource(path),
        mapper,
        "tconst", "averageRating", "numVotes"
    )

    @Bean
    fun loadRatings(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        reader: ItemReader<Rating>,
        repository: RatingRepository
    ): Step = commonStep(
        "loadRatings",
        jobRepository,
        transactionManager,
        reader,
        repository
    )
}

@Repository
interface RatingRepository : CrudRepository<Rating, String>
