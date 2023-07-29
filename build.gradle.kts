plugins {
    id("java")
    id("jacoco")
    id("maven-publish")
    id("org.sonarqube") version "3.3"
    id("io.freefair.lombok") version "6.6.1"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

jacoco {
    toolVersion = "0.8.8"
}

sonarqube  {
    properties {
        property("sonar.projectName", project.name)
        property("sonar.projectKey", "EterniaServer_EterniaLib")
        property("sonar.organization", "eterniaserver")
        property("sonar.projectVersion", "${project.version}")
        property("sonar.sources", "src/main/java")
        property("sonar.tests", "src/test/java")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.java.binaries", "build/classes")
        property("sonar.java.libraries", "build/libs")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.verbose", "true")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.junit.reportsPath", "build/test-results/test")
    }
}

group = "br.com.eterniaserver"
version = "4.0.1-BETA"

repositories {
    mavenCentral()
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    maven {
        name = "aikar-repo"
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:5.0.1") { exclude("org.slf4j", "slf4j-api") }
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    testImplementation("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-inline:5.2.0")
}

tasks.shadowJar {
    relocate("com.zaxxer.hikari", "br.com.eterniaserver.hikari")
    relocate("co.aikar.commands", "br.com.eterniaserver.acf")
    relocate("co.aikar.locales", "br.com.eterniaserver.locales")
    archiveBaseName.set(project.name)
    archiveClassifier.set("")
    archiveVersion.set("${project.version}")
}

tasks.test {
    useJUnitPlatform()

    dependsOn("cleanTest")

    testLogging {
        events("passed", "skipped", "failed")
    }

    finalizedBy("jacocoTestReport")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

tasks.named("sonarqube").configure {
    dependsOn("test")
}

tasks.named("build").configure {
    dependsOn("shadowJar")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to version))
        filteringCharset = "UTF-8"
    }
}

publishing {
    repositories {
        maven {
            name = "br.com.eterniaserver"
            url = uri("https://maven.pkg.github.com/eterniaserver/eternialib")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }

    publications {
        register<MavenPublication>("gpr") {
            project.shadow.component(this)
        }
    }
}
