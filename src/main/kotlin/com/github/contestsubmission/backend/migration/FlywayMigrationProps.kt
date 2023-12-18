package com.github.contestsubmission.backend.migration

import jakarta.enterprise.context.ApplicationScoped

/**
 * Keeps track of whether the flyway migration has been validated yet.
 * This is because only the first validation works.
 * Every subsequent validation will fuck everything up for no reason whatsoever.
 */
@ApplicationScoped
object FlywayMigrationProps {
	var hasValidatedYet = false
}
