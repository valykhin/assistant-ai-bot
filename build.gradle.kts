import java.util.Properties
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.liquibase.gradle") version "2.2.0"
	id("com.bmuschko.docker-remote-api") version "9.4.0"
}

group = "ru.ivalykhin"
version = "0.0.1-SNAPSHOT"
description = "ai-assistant"

val dockerImageName = "ghcr.io/valykhin/${rootProject.name}:${version}"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.liquibase:liquibase-core:4.31.1")
	implementation("org.telegram:telegrambots:6.9.7.1")
	implementation("com.openai:openai-java:0.35.1")

	compileOnly("org.projectlombok:lombok")

	runtimeOnly("org.postgresql:postgresql:42.7.2")
	runtimeOnly("com.h2database:h2")

	annotationProcessor("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
	testImplementation("org.testcontainers:postgresql:1.17.6")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")

	liquibaseRuntime("org.liquibase:liquibase-core:4.31.1")
	liquibaseRuntime("org.liquibase.ext:liquibase-hibernate6:4.29.2")
	liquibaseRuntime("info.picocli:picocli:4.7.6")
	liquibaseRuntime("org.postgresql:postgresql:42.7.2")
	liquibaseRuntime("org.springframework.boot:spring-boot-starter-data-jpa")
	liquibaseRuntime("org.springframework:spring-beans")

	liquibaseRuntime(files("build/classes/java/main"))

}

liquibase {
	activities.register("main") {
		val liquibaseProperties = readProperties(file("src/main/resources/db/liquibase.properties"))
		val dbPropertiesFile = file("src/main/resources/db.properties")
		var dbProperties = Properties()

		if (dbPropertiesFile.exists()) {
			dbProperties = readProperties(dbPropertiesFile)
		} else {
			dbProperties["url"] = System.getenv("SPRING_DATASOURCE_URL")
			dbProperties["username"] = System.getenv("SPRING_DATASOURCE_USERNAME")
			dbProperties["password"] = System.getenv("SPRING_DATASOURCE_PASSWORD")
		}

		this.arguments = mapOf(
				"logLevel" to (System.getenv("LIQUIBASE_LOG_LEVEL") ?: "info"),
				"changeLogFile" to liquibaseProperties["changeLogFile"],
				"driver" to liquibaseProperties["driver"],
				"referenceUrl" to liquibaseProperties["referenceUrl"],
				"referenceDriver" to liquibaseProperties["referenceDriver"],

				"url" to dbProperties["url"],
				"username" to dbProperties["username"],
				"password" to dbProperties["password"],
		)
		}
	}

tasks.jacocoTestReport {
	reports {
		xml.required.set(false)
		csv.required.set(false)
		html.required.set(true)
		html.outputLocation.set(layout.buildDirectory.dir("reports/coverage") )
	}
}

tasks.named<BootJar>("bootJar") {
	archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging{
		showStackTraces = true
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
	}
}

tasks.register<DockerBuildImage>("dockerBuild") {
	inputDir.set(file("."))
	images.add(dockerImageName)
	buildArgs.put("BUILD_IMAGE", "eclipse-temurin:17.0.11_9-jdk-centos7")
	platform.set("linux/amd64")
}

tasks.register<DockerPushImage>("dockerPush") {
//	dependsOn("dockerBuildImage")
	images.add(dockerImageName)
}


fun readProperties(propertiesFile: File) = Properties().apply {
	propertiesFile.inputStream().use { fis ->
		load(fis)
	}
}
