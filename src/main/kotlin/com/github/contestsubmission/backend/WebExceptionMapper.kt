package com.github.contestsubmission.backend

import io.quarkus.security.AuthenticationFailedException
import io.quarkus.security.UnauthorizedException
import io.quarkus.vertx.http.runtime.security.ChallengeData
import io.quarkus.vertx.http.runtime.security.HttpAuthenticator
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.RoutingContext
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.server.ServerExceptionMapper
import org.jboss.resteasy.reactive.server.exceptionmappers.AsyncExceptionMappingUtil
import kotlin.reflect.KClass

class ExceptionMappers {
	@ServerExceptionMapper(WebApplicationException::class)
	fun webApplicationExceptionMapper(t: WebApplicationException): Response =
		HttpError(
			status = t.response.status,
			throwable = t,
			message = t.message ?: t.response.statusInfo.reasonPhrase
		).map()

	@ServerExceptionMapper(UnauthorizedException::class)
	fun unauthorizedExceptionMapper(t: UnauthorizedException): Response =
		HttpError(
			status = Response.Status.UNAUTHORIZED,
			throwable = t,
			message = t.message!!
		).map()

	@ServerExceptionMapper(AuthenticationFailedException::class)
	fun authenticationFailedExceptionMapper(t: AuthenticationFailedException, routingContext: RoutingContext): Uni<Response> {
		return if ((t.message ?: t.cause?.message) != null) {
			val authenticator = routingContext.get<HttpAuthenticator>(HttpAuthenticator::class.java.name)
			val challenge = authenticator.getChallenge(routingContext)
			challenge.map { challengeData: ChallengeData? ->
				if (challengeData == null) {
					AsyncExceptionMappingUtil.DEFAULT_UNAUTHORIZED_RESPONSE
				} else {
					val status: Response.ResponseBuilder = Response.status(challengeData.status)
					if (challengeData.headerName != null) {
						status.header(challengeData.headerName.toString(), challengeData.headerContent)
					}
					status.entity(
						HttpError(
							status = challengeData.status,
							throwable = t,
							message = t.message ?: t.cause?.message ?: "Not Authenticated"
						)
					)

					status.build()
				}
			}
		} else {
			Uni.createFrom().item(AsyncExceptionMappingUtil.DEFAULT_UNAUTHORIZED_RESPONSE)
		}
	}
}

data class HttpError(val status: Int, val type: String, val message: String) {
	constructor(status: Response.Status, throwable: Throwable, message: String)
		: this(status, throwable::class, message)

	constructor(status: Response.Status, type: KClass<out Throwable>, message: String)
		: this(status.statusCode, type.simpleName!!, message)

	constructor(status: Int, throwable: Throwable, message: String)
		: this(status, throwable::class.simpleName!!, message)

	fun map(): Response = Response.status(status).entity(this).build()
}
