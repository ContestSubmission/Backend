package com.github.contestsubmission.backend.feature.mail;

import io.quarkus.mailer.MailTemplate;
import io.quarkus.qute.CheckedTemplate;

public class MailingTemplates {
	@CheckedTemplate
	static class Templates {
		public static native MailTemplate.MailTemplateInstance invite(
			String teamName,
			String contestName,
			String websiteUrl,
			String websiteName,
			String jwt
		);
	}
}
