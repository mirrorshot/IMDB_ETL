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
@IdClass(value = PrincipalID::class)
@Table(name = "principal")
data class Principal(
    @Id
    val title: String,
    @Id
    val ordering: Int,
    val person: String,
    val category: String?,
    val job: String?,
    val characters: String?
)

data class PrincipalID(
    val title: String,
    val ordering: Int
) : Serializable


@Component
class PrincipalMapper : LineMapper<Principal> {
    override fun mapLine(text: String, index: Int): Principal =
        text.split("\t")
            .let { values ->
                Principal(
                    values[0].clear()!!,
                    values[1].clear()!!.toInt(),
                    values[2].clear()!!,
                    values[3].clear(),
                    values[4].clear(),
                    values[5].clear()
                )
            }
}

@Configuration
@ConditionalOnProperty(prefix = "imdb.loading.principals", name = ["enabled"], havingValue = "true")
class PrincipalConfiguration {
    @Bean
    fun principalReader(
        mapper: PrincipalMapper,
        @Value(value = "\${imdb.loading.principals.location:data/title.principals.tsv}") path: Path
    ): ItemReader<Principal> = commonReader(
        "principalReader",
        FileSystemResource(path),
        mapper,
        "tconst",
        "ordering",
        "nconst",
        "category",
        "job",
        "characters"
    )

    @Bean
    fun loadPrincipals(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        reader: ItemReader<Principal>,
        repository: PrincipalRepository
    ): Step = commonStep(
        "loadPrincipals",
        jobRepository,
        transactionManager,
        reader,
        repository
    )
}

@Repository
interface PrincipalRepository : CrudRepository<Principal, PrincipalID>
