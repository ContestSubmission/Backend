import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.20"
	kotlin("plugin.allopen") version "1.9.20"
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
	implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom:1.9.20"))
	implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
	implementation(enforcedPlatform("${quarkusPlatformGroupId}:quarkus-amazon-services-bom:${quarkusPlatformVersion}"))
	implementation("io.quarkus:quarkus-resteasy-reactive")
	implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
	implementation("io.quarkus:quarkus-resteasy-reactive-kotlin-serialization")
	implementation("io.quarkus:quarkus-flyway")
	implementation("io.quarkus:quarkus-smallrye-openapi")
	implementation("io.quarkus:quarkus-kotlin")
	implementation("io.quarkus:quarkus-jacoco")
	implementation("io.quarkus:quarkus-opentelemetry")
	implementation("io.quarkus:quarkus-scheduler")
	implementation("io.quarkus:quarkus-hibernate-reactive-panache-kotlin")
	implementation("io.quarkiverse.amazonservices:quarkus-amazon-s3")
	implementation("io.quarkus:quarkus-reactive-pg-client")
	implementation("io.quarkus:quarkus-container-image-jib")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("io.quarkus:quarkus-arc")
	implementation("io.quarkus:quarkus-micrometer-registry-prometheus")
	// required because quarkus is too stupid to use multiple .properties files
	implementation("io.quarkus:quarkus-config-yaml")
	testImplementation("io.quarkus:quarkus-junit5")
	testImplementation("io.rest-assured:rest-assured")
}

group = "com.github.contestsubmission.backend"
version = "0.0.1"

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

allOpen {
	annotation("jakarta.ws.rs.Path")
	annotation("jakarta.enterprise.context.ApplicationScoped")
	annotation("jakarta.persistence.Entity")
	annotation("io.quarkus.test.junit.QuarkusTest")
}
