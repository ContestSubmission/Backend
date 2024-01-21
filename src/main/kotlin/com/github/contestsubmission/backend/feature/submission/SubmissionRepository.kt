package com.github.contestsubmission.backend.feature.submission

import com.github.contestsubmission.backend.util.db.CRUDRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class SubmissionRepository : CRUDRepository<Submission, UUID>(Submission::class)
