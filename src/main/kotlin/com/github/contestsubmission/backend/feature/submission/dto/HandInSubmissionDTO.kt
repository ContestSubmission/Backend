package com.github.contestsubmission.backend.feature.submission.dto

import com.github.contestsubmission.backend.feature.submission.Submission
import com.github.contestsubmission.backend.util.rest.ToEntityDTO

data class HandInSubmissionDTO(
	val jwt: String,
	val url: String
) : ToEntityDTO<Submission> {
	/**
	 * Doesn't set anything!
	 */
	override fun toEntity() = Submission()
}
