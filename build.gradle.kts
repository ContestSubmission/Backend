import io.github.simulatan.gradle.plugin.buildinfo.configuration.BuildInfoExtension
import io.github.simulatan.gradle.plugin.buildinfo.configuration.PropertiesOutputLocation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.allopen") version "1.9.22"
	kotlin("plugin.jpa") version "1.9.22"
	id("io.github.simulatan.gradle-buildinfo-plugin") version "2.1.0"
	id("io.quarkus")
}

repositories {
	mavenCentral()
	mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
	implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
	implementation(enforcedPlatform("${quarkusPlatformGroupId}:quarkus-amazon-services-bom:${quarkusPlatformVersion}"))
	implementation("io.quarkus:quarkus-resteasy-reactive")
	implementation("io.quarkus:quarkus-resteasy-reactive-kotlin")
	implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate6")
	implementation("io.quarkus:quarkus-oidc")
	implementation("io.quarkus:quarkus-smallrye-jwt")
	implementation("io.quarkus:quarkus-smallrye-jwt-build")
	implementation("io.quarkus:quarkus-flyway")
	// required for flyway... ffs
	implementation("io.quarkus:quarkus-jdbc-postgresql")
	implementation("io.quarkus:quarkus-smallrye-openapi")
	implementation("io.quarkus:quarkus-kotlin")
	implementation("io.quarkus:quarkus-jacoco")
	implementation("io.quarkus:quarkus-opentelemetry")
	implementation("io.opentelemetry.instrumentation:opentelemetry-jdbc")
	implementation("io.quarkus:quarkus-scheduler")
	implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
	implementation("io.quarkus:quarkus-hibernate-validator")
	implementation("io.quarkiverse.amazonservices:quarkus-amazon-s3")
	implementation("io.github.dyegosutil:aws-s3-presigned-post:0.1.0-beta.2")
	implementation("software.amazon.awssdk:url-connection-client")
	implementation("io.quarkus:quarkus-container-image-jib")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("io.quarkus:quarkus-arc")
	implementation("io.quarkus:quarkus-micrometer-registry-prometheus")
	implementation("io.quarkus:quarkus-kubernetes")
	implementation("io.quarkus:quarkus-kubernetes-config")
	// required because quarkus is too stupid to use multiple .properties files
	implementation("io.quarkus:quarkus-config-yaml")
	implementation("io.quarkus:quarkus-qute")
	implementation("io.quarkus:quarkus-mailer")

	quarkusDev("io.quarkiverse.mailpit:quarkus-mailpit:0.0.9")

	testImplementation("io.quarkus:quarkus-junit5")
	testImplementation("io.rest-assured:rest-assured")

	quarkusDev("org.jetbrains.kotlin:kotlin-allopen-compiler-plugin")
}

group = "com.github.contestsubmission.backend"
version = "3.1.0"

buildInfo {
	this.gitInfoMode = BuildInfoExtension.MODE_ERROR
	this.propertiesOutputs = listOf(PropertiesOutputLocation { project ->
		listOf(project.layout.buildDirectory.get().file("buildinfo.properties").asFile)
	})
	this.extraAttribute("Version", version)
	this.extraAttribute("Application", rootProject.name)
}

tasks.register("copyBuildInfoToJar", Copy::class) {
	group = "build"
	description = "Copies build info to resources"
	dependsOn(tasks.buildInfo)
	from("build/buildinfo.properties")
	into("build/resources/main")
}

listOf(tasks.jar, tasks.quarkusDependenciesBuild, tasks.quarkusDev, tasks.imageBuild)
	.map(TaskProvider<out Task>::get)
	.forEach {
		it.dependsOn(tasks.getByName("copyBuildInfoToJar"))
	}

tasks.withType<JavaCompile> {
	// required for qute - the templates utilize native methods that need to have their parameter names captured
	options.compilerArgs.add("-parameters")
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = JavaVersion.VERSION_21.toString()
	kotlinOptions.javaParameters = true
}

kotlin {
	jvmToolchain(JavaVersion.VERSION_21.majorVersion.toInt())
}

tasks.withType<Test> {
	systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
val allOpenAnnotationArguments = listOf(
	"jakarta.ws.rs.Path",
	"jakarta.enterprise.context.ApplicationScoped",
	"jakarta.enterprise.context.RequestScoped",
	"jakarta.persistence.Entity",
	"io.quarkus.test.junit.QuarkusTest"
)

allOpen {
	annotations(allOpenAnnotationArguments)
}

val disabledKubeConfig = Pair("KUBERNETES_AUTH_TRYKUBECONFIG", "false")

tasks.test {
	setEnvironment(disabledKubeConfig)
}

tasks.quarkusDev {
	compilerOptions {
		compiler("kotlin").args(
			mutableListOf(
				"-Xplugin=${configurations.quarkusDev.get().files.find { "kotlin-allopen-compiler-plugin" in it.name }}"
			) + allOpenAnnotationArguments.map { "-P=plugin:org.jetbrains.kotlin.allopen:annotation=$it" }
		)
	}
	// "disable" k8s connection as quarkus can't be bothered to respect the config value
	environmentVariables.put(disabledKubeConfig)
}

fun <K : Any, V : Any> MapProperty<K, V>.put(pair: Pair<K, V>): MapProperty<K, V> {
	this.put(pair.first, pair.second)
	return this
}
