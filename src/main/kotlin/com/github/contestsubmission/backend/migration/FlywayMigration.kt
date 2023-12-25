package com.github.contestsubmission.backend.migration

import com.github.contestsubmission.backend.migration.FlywayMigrationProps.hasValidatedYet
import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import io.quarkus.scheduler.Scheduler
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.flywaydb.core.Flyway
import org.hibernate.SessionFactory

@Startup
/**
 * This class is required because quarkus can't even run a simple flyway migration on startup.
 * Epic reactive fail.
 */
class FlywayMigration internal constructor(
	scheduler: Scheduler,
	sessionFactory: SessionFactory,
	@ConfigProperty(name = "quarkus.datasource.reactive.url") datasourceUrl: String,
	@ConfigProperty(name = "quarkus.datasource.username") datasourceUsername: String,
	@ConfigProperty(name = "quarkus.datasource.password") datasourcePassword: String,
	@ConfigProperty(name = "quarkus.flyway.enabled") flywayEnabled: Boolean,
	@ConfigProperty(name = "quarkus.flyway.clean-at-start") flywayCleanAtStart: Boolean
) {
	init {
		val flyway = Flyway
			.configure()
			.dataSource(datasourceUrl.replace("vertx-reactive:", "jdbc:"), datasourceUsername, datasourcePassword)
			.cleanDisabled(!flywayCleanAtStart)
			.load()
		if (flywayEnabled) {
			Log.info("Running flyway migration...")
			scheduler.pause()
			if (flywayCleanAtStart) {
				flyway.clean()
			}
			flyway.migrate()
			// little hack to FIX STUPID FUCKING QUARKUS MISBEHAVING YET AGAIN
			// I SWEAR TO GOD WHO THE FUCK CREATED THIS GARBAGE
			// ITS JUST NOT WORKING 99% OF THE TIME AND THE REST IT JUST SUCKS ASS
			// WHY THE FUCK DOES THIS METHOD BREAK AND BLOCK THE FUCKING WEBSERVER THREAD????
			if (!hasValidatedYet) {
				Log.info("Validating mapped objects...")
				hasValidatedYet = true
				sessionFactory.schemaManager
					.validateMappedObjects()
			}
			scheduler.resume()
			Log.info("Flyway migration complete!")
		}
	}
}
