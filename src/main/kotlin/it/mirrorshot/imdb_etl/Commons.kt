package it.mirrorshot.imdb_etl

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Step
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.LineMapper
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.core.io.FileSystemResource
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.PlatformTransactionManager

class IdentityProcessor<T : Any> : ItemProcessor<T, T> {
    override fun process(item: T): T = item

}

fun <T : Any> commonReader(
    name: String,
    resource: FileSystemResource,
    mapper: LineMapper<T>,
    vararg columns: String,
    header: Boolean = true
): ItemReader<T> = FlatFileItemReaderBuilder<T>()
    .name(name)
    .resource(resource)
    .linesToSkip(if (header) 1 else 0)
    .delimited()
    .names(*columns)
    .lineMapper(mapper)
    .build()

/**
 * Used to create a flow Step with a CommonWriter on the given CrudRepository
 */
fun <T : Any, ID: Any> commonStep(
    name: String,
    jobRepository: JobRepository,
    transactionManager: PlatformTransactionManager,
    reader: ItemReader<T>,
    repository: CrudRepository<T, ID>,
    chunkSize: Int = 2500,
    processor: ItemProcessor<T, T> = IdentityProcessor()
): Step = commonStep(
    name,
    jobRepository,
    transactionManager,
    reader,
    chunkSize,
    processor,
    CommonWriter(repository))

fun <T : Any> commonStep(
    name: String,
    jobRepository: JobRepository,
    transactionManager: PlatformTransactionManager,
    reader: ItemReader<T>,
    chunkSize: Int = 2500,
    processor: ItemProcessor<T, T> = IdentityProcessor(),
    writer: ItemWriter<T>
): Step = StepBuilder(name, jobRepository)
    .chunk<T, T>(chunkSize, transactionManager)
    .reader(reader)
    .processor(processor)
    .writer(writer)
    .build()

class CommonWriter<T : Any, I: Any>(
    private val repository: CrudRepository<T, I>
) : ItemWriter<T> {

    private var counter = 0

    override fun write(chunk: Chunk<out T>) {
        log.info("loading chunk {} of {} items", counter++, chunk.size())
        val start = System.currentTimeMillis()
        chunk.forEach { item ->
            try {
                repository.save(item)
            } catch (e: Exception) {
                log.error("failed on {}", item, e)
                chunk.skip(e)
            }
        }
        log.info("chunk loaded in: {} s", (System.currentTimeMillis() - start) / 1000.0)
    }

    companion object {
        private val log = LoggerFactory.getLogger(CommonWriter::class.java)
    }
}
