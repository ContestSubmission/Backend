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
	fun getByContest(contestId: UUID, personId: UUID?): List<GradeTeamOverviewDTO> {
		return entityManager.createQuery(
			"""
			SELECT ${EnumeratedTeamDTO.toJPAQuery("t")} AS team,
				s AS submission,
				COUNT(g.score) AS count,
				COALESCE(SUM(g.score), 0) AS score,
				persGrade AS personalGrade
			FROM Team t
			LEFT JOIN t.submissions s
			LEFT JOIN Grade g ON g.submission = s
			LEFT JOIN Grade persGrade ON persGrade.submission = s AND persGrade.person.id = :personId
			WHERE t.contest.id = :contestId
				AND (s.handedInAt >= ALL(
					SELECT sub.handedInAt
					FROM Submission sub
					WHERE sub.team = t
				))
			GROUP BY t, t.owner, s, persGrade
		""".trimIndent(), Tuple::class.java)
			.setParameter("contestId", contestId)
			.setParameter("personId", personId)
			.resultList
			.map {
				GradeTeamOverviewDTO(
					submission = it.getOrNull("submission", Submission::class.java),
					team = it.get("team", EnumeratedTeamDTO::class.java),
					scoreCount = it.get("count", Long::class.java),
					score = it.get("score", Long::class.java),
					personalGrade = it.getOrNull("personalGrade", Grade::class.java)
				)
			}
	}
}
