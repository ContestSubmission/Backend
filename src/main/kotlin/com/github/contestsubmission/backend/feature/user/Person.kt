package com.github.contestsubmission.backend.feature.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
class Person(
	@GeneratedValue(strategy = GenerationType.UUID)
	@Id
	var id: UUID? = null,
	val name: String? = null
)
