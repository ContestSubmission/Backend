package com.github.contestsubmission.backend.setup

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
/**
 * This lifecycle hook is used to set up the AWS S3 credentials globally.
 * It is required because the quarkus s3 extension doesn't register the credentials for anything but the quarkus managed s3 client
 */
class S3AccessSetup {

	@ConfigProperty(name = "quarkus.s3.aws.credentials.static-provider.access-key-id")
	lateinit var accessKeyId: String

	@ConfigProperty(name = "quarkus.s3.aws.credentials.static-provider.secret-access-key")
	lateinit var secretAccessKey: String

	@ConfigProperty(name = "quarkus.s3.endpoint-override")
	lateinit var endpointOverride: String

	fun onStart(@Observes startupEvent: StartupEvent) {
		System.setProperty("aws.accessKeyId", accessKeyId)
		System.setProperty("aws.secretAccessKey", secretAccessKey)
		System.setProperty("aws.s3.endpointOverride", endpointOverride)
	}
}
