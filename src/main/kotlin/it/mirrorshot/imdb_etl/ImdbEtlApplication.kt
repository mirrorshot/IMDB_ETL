package it.mirrorshot.imdb_etl

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import kotlin.system.exitProcess

@SpringBootApplication
class ImdbEtlApplication {

    @Bean
    fun executor(): TaskExecutor = SyncTaskExecutor()

    @Bean
    fun importIMDB(
        env: Environment,
        jobRepository: JobRepository,
        listener: JobExecutionListener,
        @Autowired(required = false) @Qualifier(value = "loadTitles") loadTitles: Step?,
        @Autowired(required = false) @Qualifier(value = "loadEpisodes") loadEpisodes: Step?,
        @Autowired(required = false) @Qualifier(value = "loadAkas") loadAkas: Step?,
        @Autowired(required = false) @Qualifier(value = "loadRatings") loadRatings: Step?,
        @Autowired(required = false) @Qualifier(value = "loadPeople") loadPeople: Step?,
        @Autowired(required = false) @Qualifier(value = "loadPrincipals") loadPrincipals: Step?,
        @Autowired(required = false) @Qualifier(value = "loadCrews") loadCrews: Step?
    ): Job {
        val steps = listOfNotNull(
            loadTitles,
            loadEpisodes,
            loadAkas,
            loadRatings,
            loadPeople,
            loadPrincipals,
            loadCrews
        )
        val jb = JobBuilder("importIMBD", jobRepository)
            .incrementer(RunIdIncrementer())
            .listener(listener)
        return when (steps.size) {
            1 -> jb.flow(steps[0]).end()
            2 -> jb.flow(steps[0]).next(steps[1]).end()
            3 -> jb.flow(steps[0]).next(steps[1]).next(steps[2]).end()
            4 -> jb.flow(steps[0]).next(steps[1]).next(steps[2]).next(steps[3]).end()
            5 -> jb.flow(steps[0]).next(steps[1]).next(steps[2]).next(steps[3]).next(steps[4]).end()
            6 -> jb.flow(steps[0]).next(steps[1]).next(steps[2]).next(steps[3]).next(steps[4]).next(steps[5]).end()
            7 -> jb.flow(steps[0]).next(steps[1]).next(steps[2]).next(steps[3]).next(steps[4]).next(steps[5]).next(steps[6]).end()

            else -> throw IllegalArgumentException("no step configured")
        }.build()
    }

}

fun main(args: Array<String>) {
    exitProcess(SpringApplication.exit(SpringApplication.run(ImdbEtlApplication::class.java, *args)))
}
