package com.github.contestsubmission.backend.feature.grade

import com.github.contestsubmission.backend.feature.grade.dto.GradeTeamOverviewDTO
import com.github.contestsubmission.backend.feature.submission.Submission
import com.github.contestsubmission.backend.feature.team.dto.EnumeratedTeamDTO
import com.github.contestsubmission.backend.util.db.CRUDRepository
import com.github.contestsubmission.backend.util.db.getOrNull
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.Tuple
import java.util.UUID

@ApplicationScoped
class GradeRepository : CRUDRepository<Grade, GradeId>(Grade::class) {
	fun getByContest(contestId: UUID): List<GradeTeamOverviewDTO> {
		return entityManager.createQuery("""
			SELECT ${EnumeratedTeamDTO.toJPAQuery("t")} AS team,
				s AS submission,
				COUNT(g.score) AS count,
				COALESCE(SUM(g.score), 0) AS score
			FROM Team t
			LEFT JOIN t.submissions s
			LEFT JOIN Grade g ON g.submission = s
			WHERE t.contest.id = :contestId
				AND (s.handedInAt >= ALL(
					SELECT sub.handedInAt
					FROM Submission sub
					WHERE sub.team = t
				))
			GROUP BY t, t.owner, g, g.submission, s
		""".trimIndent(), Tuple::class.java)
			.setParameter("contestId", contestId)
			.resultList
			.map {
				GradeTeamOverviewDTO(
					it.getOrNull("submission", Submission::class.java),
					it.get("team", EnumeratedTeamDTO::class.java),
					it.get("count", Long::class.java),
					it.get("score", Long::class.java)
				)
			}
	}
}
