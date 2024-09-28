package io.tohuwabohu.crud.util

import io.quarkus.arc.Arc
import io.quarkus.flyway.runtime.FlywayContainerProducer
import io.quarkus.flyway.runtime.QuarkusPathLocationScanner
import io.quarkus.logging.Log
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.flywaydb.core.Flyway
import java.util.*
import javax.sql.DataSource


/**
 * Workaround to hook migration on startup. Currently, JDBC drivers and reactive datasources conflict with each other
 * and Hibernate reactive is not supported out of the box.
 *
 * https://github.com/quarkusio/quarkus/issues/10716
 */
@ApplicationScoped
class FlywayMigration {
    @ConfigProperty(name = "quarkus.flyway.migrate-at-start")
    private lateinit var migrateAtStart: Optional<Boolean>

    @ConfigProperty(name = "quarkus.datasource.reactive.url")
    private lateinit var datasourceUrl: String

    @ConfigProperty(name = "quarkus.datasource.username")
    private lateinit var datasourceUsername: String

    @ConfigProperty(name = "quarkus.datasource.password")
    private lateinit var datasourcePassword: String

    @ConfigProperty(name = "librefit.flyway.migration.folder")
    private lateinit var flywayMigrationFolder: String

    @ConfigProperty(name = "librefit.flyway.migration.files")
    private lateinit var flywayMigrationFiles: List<String>

    fun runFlywayMigration(@Observes event: StartupEvent?) {
        val migrate = migrateAtStart.isPresent && migrateAtStart.get()

        Log.info("Checking migration... flag is set to $migrate.")

        if (migrate) {
            Log.info("Flyway migration files set to $flywayMigrationFiles in folder $flywayMigrationFolder.")

            flywayMigrationFiles = flywayMigrationFiles.map { file -> "$flywayMigrationFolder/$file" }

            QuarkusPathLocationScanner.setApplicationMigrationFiles(flywayMigrationFiles)

            val ds: DataSource = Flyway.configure().dataSource("jdbc:$datasourceUrl", datasourceUsername, datasourcePassword).dataSource
            val flywayProducer = Arc.container().instance(FlywayContainerProducer::class.java).get()

            val flywayContainer = flywayProducer.createFlyway(ds, "<default>", true, true)
            val flyway = flywayContainer.flyway
            flyway.migrate()
        }
    }
}
