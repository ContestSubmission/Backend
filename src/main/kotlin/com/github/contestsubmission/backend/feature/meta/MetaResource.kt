package com.github.contestsubmission.backend.feature.meta

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.event.Observes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.lang.management.ManagementFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Path("/")
class MetaResource {

	companion object {
		val BUILDINFO_DATE_PATTERN: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")
	}

	data class ApplicationInformation(
		val name: String,
		val version: String,
		val buildTime: LocalDateTime,
		val buildCommit: String,
		val buildCommitDate: LocalDateTime,
		val buildBranch: String?,
		val serverTime: LocalDateTime = LocalDateTime.MIN,
		@field:Schema(description = "The uptime of the server in seconds", required = true)
		val uptime: Long = -1
	)

	val buildInfo: Properties = Properties()
	lateinit var baseApplicationInformation: ApplicationInformation

	// if quarkus didn't suck so bad, `ObservesAsync` would be way better
	fun onStartup(@Observes startupEvent: StartupEvent) {
		val resources = javaClass.getClassLoader()
			.getResources("buildinfo.properties")

		check(resources.hasMoreElements()) { "No buildinfo found" }
		buildInfo.load(resources.nextElement().openStream())
		check(!resources.hasMoreElements()) { "Multiple buildinfo files found" }

		baseApplicationInformation = ApplicationInformation(
			name = name,
			version = version,
			buildTime = buildInfo.getProperty("Build-Date")?.let { LocalDateTime.parse(it, BUILDINFO_DATE_PATTERN) }!!,
			buildCommit = buildInfo.getProperty("Git-Commit")!!,
			buildCommitDate = buildInfo.getProperty("Git-Committer-Date")?.let { LocalDateTime.parse(it, BUILDINFO_DATE_PATTERN) }!!,
			buildBranch = buildInfo.getProperty("Git-Branch"),
		)
	}

	@ConfigProperty(name = "quarkus.application.name")
	protected lateinit var name: String

	@ConfigProperty(name = "quarkus.application.version")
	protected lateinit var version: String

	@Path("info")
	@GET
	fun info(): ApplicationInformation {
		return baseApplicationInformation.copy(
			serverTime = LocalDateTime.now(),
			// uptime in seconds (originally in ms)
			uptime = ManagementFactory.getRuntimeMXBean().uptime / 1000L
		)
	}
}
