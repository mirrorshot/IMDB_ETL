package it.mirrorshot.imdb_etl

import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class JobCompletionListener : JobExecutionListener {
    override fun afterJob(jobExecution: JobExecution) {
        log.info("job closed with: {}", jobExecution.allFailureExceptions)
        if (jobExecution.status == BatchStatus.COMPLETED) {
            log.info(
                "job finished in: {}ms", ChronoUnit.MILLIS.between(
                    jobExecution.endTime,
                    jobExecution.startTime
                )
            )
        }
    }

    override fun beforeJob(jobExecution: JobExecution) {
        log.info("starting job: {}", jobExecution)
    }

    companion object {
        private val log = LoggerFactory.getLogger(JobCompletionListener::class.java)
    }
}
