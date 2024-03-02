package com.github.contestsubmission.backend.setup

import io.quarkus.logging.LoggingFilter
import java.util.logging.Filter
import java.util.logging.LogRecord

@LoggingFilter(name = "OpenTelemetryExporterFilter")
/**
 * Removes annoying log messages from the OpenTelemetry exporter.<br>
 *
 * - In dev, the tracing backend is optional and not always available.
 * - In prod, the actual exporter is injected => the failure is irrelevant.
 */
class OpenTelemetryExporterFilter : Filter {
	override fun isLoggable(record: LogRecord): Boolean {
		// OTel problems that aren't related to sending the data are still relevant
		// therefore, this filter only removes the "Failed to export <entity>. The request could not be executed" messages
		return !(record.message.contains("Failed to export") && record.message.contains("The request could not be executed."))
	}
}
