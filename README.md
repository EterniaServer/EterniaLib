# EterniaLib ![EterniaLib Build Status](https://github.com/EterniaServer/EterniaLib/actions/workflows/build.yml/badge.svg) ![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=EterniaServer_EterniaLib&metric=vulnerabilities) ![Coverage](https://sonarcloud.io/api/project_badges/measure?project=EterniaServer_EterniaLib&metric=coverage)

A base library to agile plugin development. **([Wiki](https://github.com/EterniaServer/EterniaLib/wiki))**

**Only works with Paper or Paper forks**

## How to use (Plugin Developers)

**Gradle (Kotlin):**
```kotlin
repositories {
    maven {
        name = "eternialib"
        url = uri("https://maven.pkg.github.com/eterniaserver/eternialib")
    }
}

dependencies {
    compileOnly("br.com.eterniaserver:eternialib:4.0.0")
}
```
**Gradle (Groovy):**
```groovy
repositories {
    maven {
        name "eternialib"
        url "https://maven.pkg.github.com/eterniaserver/eternialib"
    }
}

dependencies {
    compileOnly "br.com.eterniaserver:eternialib:4.0.0"
}
```
**Maven:**
```xml
<project>
    <repositories>
        <repository>
            <id>eternialib</id>
            <url>https://maven.pkg.github.com/eterniaserver/eternialib</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>br.com.eterniaserver</groupId>
            <artifactId>eternialib</artifactId>
            <version>4.0.0</version>
        </dependency>
    </dependencies>
</project>
```
### [Wiki](https://github.com/EterniaServer/EterniaLib/wiki) (Plugin Developers)


## Special Thanks

**Annotation Command Framework (ACF)**
- **https://github.com/aikar/commands**
- Thanks for creating such a complete command framework.
