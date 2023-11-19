package com.github.contestsubmission.backend.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class MyTestEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long? = null

	val name: String? = null
}
