package com.github.contestsubmission.backend.feature.mail

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class MailerService {
	fun sendInviteMail(email: String, contestId: UUID, jwt: String) {
		Log.info("Sending invite mail for contest $contestId to $email with jwt $jwt")
	}
}
