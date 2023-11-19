package com.github.contestsubmission.backend.resource

import com.github.contestsubmission.backend.util.transaction
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.hibernate.reactive.mutiny.Mutiny

@Path("/mytest")
class MyTestResource {

	@Inject
	lateinit var sf: Mutiny.SessionFactory

	@Path("list")
	@GET
	suspend fun list(): List<String>? =
		sf.transaction {
			it.createQuery("select e.name from MyTestEntity e", String::class.java)
				.resultList
		}
}
