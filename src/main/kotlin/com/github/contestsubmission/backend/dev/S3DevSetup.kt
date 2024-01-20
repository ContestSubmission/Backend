package com.github.contestsubmission.backend.dev

import io.quarkus.runtime.StartupEvent
import io.quarkus.runtime.configuration.ConfigUtils
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
/**
 * This class is used to set up the AWS S3 credentials for the dev profile.
 * It is required because the quarkus s3 extension doesn't register the credentials for anything but the quarkus managed s3 client
 */
class S3DevSetup {

	@ConfigProperty(name = "quarkus.s3.aws.credentials.static-provider.access-key-id")
	lateinit var accessKeyId: String

	@ConfigProperty(name = "quarkus.s3.aws.credentials.static-provider.secret-access-key")
	lateinit var secretAccessKey: String

	@ConfigProperty(name = "quarkus.s3.endpoint-override")
	lateinit var endpointOverride: String

	fun onStart(@Observes startupEvent: StartupEvent) {
		if (ConfigUtils.getProfiles().contains("dev")) {
			System.setProperty("aws.accessKeyId", accessKeyId)
			System.setProperty("aws.secretAccessKey", secretAccessKey)
			System.setProperty("aws.s3.endpointOverride", endpointOverride)
		}
	}
}
