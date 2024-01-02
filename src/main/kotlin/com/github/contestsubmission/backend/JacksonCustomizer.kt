package com.github.contestsubmission.backend

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module
import io.quarkus.jackson.ObjectMapperCustomizer
import jakarta.inject.Singleton

@Singleton
class JacksonCustomizer : ObjectMapperCustomizer {
	override fun customize(objectMapper: ObjectMapper) {
		objectMapper.registerModule(Hibernate6Module())
	}
}
