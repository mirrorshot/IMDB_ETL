package it.mirrorshot.imdb_etl.entities

import it.mirrorshot.imdb_etl.*
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.batch.core.Step
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.file.LineMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.PlatformTransactionManager

@Entity
@Table(name = "title")
data class Title(
    @Id
    val title: String,
    val type: String,
    @Column(name = "primary_t")
    val primary: String,
    @Column(name = "original_t")
    val original: String,
    val isAdult: Boolean,
    val startYear: Int?,
    val endYear: Int?,
    val runtimeMinutes: Int?,
    val genres: String?
)

@Component
class TitleMapper : LineMapper<Title> {
    override fun mapLine(text: String, index: Int): Title =
        text.split("\t")
            .let { values ->
                Title(
                    values[0].clear()!!,
                    values[1].clear()!!,
                    values[2].clear()!!,
                    values[3].clear()!!,
                    values[4].clear()!!.asBoolean(),
                    values[5].clear()?.toIntOrNull(),
                    values[6].clear()?.toIntOrNull(),
                    values[7].clear()?.toIntOrNull(),
                    values[8].clear()
                )
            }
}

@Configuration
class TitleConfiguration {
    @Bean
    fun titleReader(
        mapper: TitleMapper
    ): ItemReader<Title> = commonReader(
        "titleReader",
        FileSystemResource("data/title.basics/data.tsv"),
        mapper,
        "tconst",
        "titleType",
        "primaryTitle",
        "originalTitle",
        "isAdult",
        "startYear",
        "endYear",
        "runtimeMinutes",
        "genres"
    )

    @Bean
    fun loadTitles(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        reader: ItemReader<Title>,
        repository: TitleRepository
    ): Step = commonStep(
        "loadTitles",
        jobRepository,
        transactionManager,
        reader,
        repository
    )

}

@Repository
interface TitleRepository : CrudRepository<Title, String>
