package it.mirrorshot.imdb_etl

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import kotlin.system.exitProcess

@SpringBootApplication
class ImdbEtlApplication {

    @Bean
    fun executor(): TaskExecutor = SyncTaskExecutor()

    @Bean
    fun importIMDB(
        jobRepository: JobRepository,
        listener: JobExecutionListener,
        @Qualifier(value = "loadTitles") loadTitles: Step,
        @Qualifier(value = "loadEpisodes") loadEpisodes: Step
    ): Job = JobBuilder("importIMBD", jobRepository)
        .incrementer(RunIdIncrementer())
        .listener(listener)
        .flow(loadTitles)
        .next(loadEpisodes)
        .end()
        .build()

}

fun main(args: Array<String>) {
    exitProcess(SpringApplication.exit(SpringApplication.run(ImdbEtlApplication::class.java, *args)))
}
