package com.github.contestsubmission.backend.util.exception

class DTOMappingException : RuntimeException {
	constructor(message: String) : super(message)
	constructor(message: String, cause: Throwable) : super(message, cause)
	constructor(cause: Throwable) : super("Error mapping DTO!", cause)
}
