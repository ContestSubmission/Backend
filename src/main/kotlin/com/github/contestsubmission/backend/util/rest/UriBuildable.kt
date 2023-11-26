package com.github.contestsubmission.backend.util.rest

import jakarta.ws.rs.core.UriInfo

interface UriBuildable {
	val uriInfo: UriInfo

	fun <T> T.toUri(): String = toUri(uriInfo)
}
