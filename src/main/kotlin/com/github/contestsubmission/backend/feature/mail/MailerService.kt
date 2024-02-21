package com.github.contestsubmission.backend.feature.mail

import com.github.contestsubmission.backend.feature.team.Team
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.net.URL


@ApplicationScoped
class MailerService {
	@ConfigProperty(name = "websiteUrl")
	protected lateinit var websiteUrl: String

	fun sendInviteMail(email: String, team: Team, jwt: String): Uni<Void> {
		Log.info("Sending invite mail for contest ${team.contest.id} to $email with jwt $jwt")

		val websiteName = URL(websiteUrl).host
		return MailingTemplates.Templates.invite(
			team.name,
			team.contest.name,
			websiteUrl,
			websiteName,
			jwt
		)!!
			.to(email)
			.subject("ContestSubmission invite")
			.send()
	}
}
