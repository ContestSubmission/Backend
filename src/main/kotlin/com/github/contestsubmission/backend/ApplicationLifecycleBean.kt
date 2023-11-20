package com.github.contestsubmission.backend

import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.jboss.logging.Logger

@ApplicationScoped
class ApplicationLifecycleBean {
	companion object {
		val LOGGER = Logger.getLogger(ApplicationLifecycleBean::class.java)!!
	}

	fun onStart(@Observes event: StartupEvent) {
		LOGGER.info("The application is starting...")
		// print system + JDK information
		LOGGER.info("""
			System information:
				OS: ${System.getProperty("os.name")} ${System.getProperty("os.version")} ${System.getProperty("os.arch")}
				Java: ${System.getProperty("java.version")} ${System.getProperty("java.vendor")}
				JVM: ${System.getProperty("java.vm.name")} ${System.getProperty("java.vm.version")} ${System.getProperty("java.vm.vendor")}
				Runtime: ${System.getProperty("java.runtime.name")} ${System.getProperty("java.runtime.version")}
				User: ${System.getProperty("user.name")}
		""".trimIndent())
	}

	fun onStop(@Observes event: ShutdownEvent) {
		LOGGER.info("The application is stopping...")
	}
}
