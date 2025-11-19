object Constants {
    const val PROJECT_VERSION = "4.5.6"

    const val JAVA_VERSION = "21"
    const val JACOCO_VERSION = "0.8.12"

    const val PAPER_VERSION = "1.21.4-R0.1-SNAPSHOT"
    const val HIKARI_VERSION = "6.2.1"
    const val ACF_VERSION = "0.5.1-SNAPSHOT"
    const val JUPITER_VERSION = "5.11.4"
    const val MOCKITO_VERSION = "5.14.2"
    const val BSTATS_VERSION = "3.1.0"
}

plugins {
    id("java")
    id("maven-publish")
    id("jacoco")
    id("org.sonarqube") version("7.0.1.6134")
    id("io.freefair.lombok") version("9.1.0")
    id("com.gradleup.shadow") version("9.2.2")
}

jacoco {
    toolVersion = Constants.JACOCO_VERSION
}

sonar {
    properties {
        property("sonar.projectKey", "EterniaServer_EterniaLib")
        property("sonar.projectVersion", "${project.version}")
        property("sonar.organization", "eterniaserver")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.test.inclusions", "**/*Test.java,**/Test*.java")
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.exclusions", "**/test/**,**/*Test.java,**/Test*.java")
        property("sonar.java.source", "21")
    }
}

group = "br.com.eterniaserver"
version = Constants.PROJECT_VERSION

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
        languageVersion.set(JavaLanguageVersion.of(Constants.JAVA_VERSION))
    }
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", Constants.PAPER_VERSION)
    implementation("com.zaxxer", "HikariCP", Constants.HIKARI_VERSION) {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("org.bstats", "bstats-bukkit", Constants.BSTATS_VERSION)
    implementation("co.aikar", "acf-paper", Constants.ACF_VERSION)
    testRuntimeOnly("org.junit.platform", "junit-platform-launcher")
    testImplementation("io.papermc.paper", "paper-api", Constants.PAPER_VERSION)
    testImplementation(platform("org.junit:junit-bom:${Constants.JUPITER_VERSION}"))
    testImplementation("org.junit.jupiter", "junit-jupiter")
    testImplementation("org.mockito", "mockito-core", Constants.MOCKITO_VERSION)
    testImplementation("org.mockito", "mockito-junit-jupiter", Constants.MOCKITO_VERSION)
}

tasks.shadowJar {
    relocate("org.bstats", "br.com.eterniaserver.bstats")
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
    val mockitoJar = configurations.testRuntimeClasspath
        .get()
        .filter { it.name.contains("mockito-core") }
        .firstOrNull()

    jvmArgs = listOf("-javaagent:$mockitoJar")

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

tasks.named("sonarResolver") {
    outputs.upToDateWhen { false }
    doFirst {
        project.layout.buildDirectory.get().asFile.mkdirs()
    }
}

tasks.named("sonar") {
    dependsOn(tasks.jacocoTestReport)
}

tasks.processResources {
    filesMatching("paper-plugin.yml") {
        expand(mapOf("version" to version))
        filteringCharset = "UTF-8"
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

afterEvaluate {
    tasks.named("generateMetadataFileForGprPublication") {
        dependsOn(tasks.named("jar"))
        dependsOn(tasks.named("shadowJar"))
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
            from(components["shadow"])
            artifact(sourcesJar.get())
        }
    }
}
