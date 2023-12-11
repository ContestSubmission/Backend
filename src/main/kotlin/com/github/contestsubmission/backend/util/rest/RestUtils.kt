package com.github.contestsubmission.backend.util.rest

import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.ResponseBuilder
import jakarta.ws.rs.core.UriInfo

fun <T> T?.response(): Response =
	when {
		this != null -> Response.ok(this).build()
		else -> Response.status(Response.Status.NOT_FOUND).build()
	}

fun <T> T.response(builder: ResponseBuilder.() -> Unit): Response = Response.ok(this).apply(builder).build()

fun <T> T.toUri(uriInfo: UriInfo): String = uriInfo.absolutePathBuilder.path(this.toString()).build().toString()
