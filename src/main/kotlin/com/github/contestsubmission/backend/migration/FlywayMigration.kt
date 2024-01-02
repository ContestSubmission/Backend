package com.github.contestsubmission.backend.migration

import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import io.quarkus.scheduler.Scheduler
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.flywaydb.core.Flyway

@Startup
/**
 * This class is required because quarkus can't even run a simple flyway migration on startup.
 * Epic reactive fail.
 */
class FlywayMigration internal constructor(
	scheduler: Scheduler,
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
			scheduler.resume()
			Log.info("Flyway migration complete!")
		}
	}
}
