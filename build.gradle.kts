plugins {
    id("java")
    id("maven-publish")
    id("jacoco")
    id("org.sonarqube") version("4.4.1.3373")
    id("io.freefair.lombok") version("8.6")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

jacoco {
    toolVersion = "0.8.11"
}

sonar {
    properties {
        property("sonar.projectKey", "EterniaServer_EterniaLib")
        property("sonar.projectVersion", "${project.version}")
        property("sonar.organization", "eterniaserver")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.scm.disabled", true)
        property("sonar.junit.reportPaths", "${project.buildDir}/test-results/test")
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.buildDir}/reports/jacoco/test/jacocoTestReport.xml")
    }
}

group = "br.com.eterniaserver"
version = "4.3.7"

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
    compileOnly("io.papermc.paper", "paper-api", "1.20.4-R0.1-SNAPSHOT")
    implementation("com.zaxxer", "HikariCP", "5.1.0") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    testImplementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.2.0")
}

tasks.shadowJar {
    relocate("com.zaxxer.hikari", "br.com.eterniaserver.hikari")
    relocate("co.aikar.commands", "br.com.eterniaserver.acf")
    relocate("co.aikar.locales", "br.com.eterniaserver.locales")
    archiveBaseName.set(project.name)
    archiveClassifier.set("")
    archiveVersion.set("${project.version}")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
        csv.required = true
    }
}

tasks.sonar {
    dependsOn(tasks.jacocoTestReport)
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
