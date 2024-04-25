package com.github.contestsubmission.backend.util

import com.github.contestsubmission.backend.feature.contest.ContestRepository
import com.github.contestsubmission.backend.feature.contest.dto.PersonalContestDTO
import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.util.db.CRUDRepository
import jakarta.ws.rs.NotFoundException
import java.util.*

fun ContestRepository.getPersonalContest(caller: Person, contestId: UUID): PersonalContestDTO
	= getPersonalContestOrNull(caller, contestId) ?: throw NotFoundException("Contest not found!")

fun <T : Any, I> CRUDRepository<T, I>.findById(id: I): T {
	return findByIdOrNull(id) ?: throw NotFoundException(entityNotFound(entityName))
}

fun entityNotFound(entityName: String) = "$entityName not found!"
