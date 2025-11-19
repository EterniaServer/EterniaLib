# EterniaLib

[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=EterniaServer_EterniaLib&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=EterniaServer_EterniaLib)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=EterniaServer_EterniaLib&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=EterniaServer_EterniaLib)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=EterniaServer_EterniaLib&metric=code_smells)](https://sonarcloud.io/dashboard?id=EterniaServer_EterniaLib)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=EterniaServer_EterniaLib&metric=sqale_index)](https://sonarcloud.io/dashboard?id=EterniaServer_EterniaLib)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=EterniaServer_EterniaLib&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=EterniaServer_EterniaLib)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=EterniaServer_EterniaLib&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=EterniaServer_EterniaLib)

A base library to agile plugin development. **([Wiki](https://github.com/EterniaServer/EterniaLib/wiki))**

**Only works with Paper or Paper forks**

## How to use (Server Owners)

Access the [package repository](https://github.com/EterniaServer/EterniaLib/packages/1892414) and download the latest version of the plugin.

## How to use (Plugin Developers)

**Gradle (Kotlin):**
```kotlin
repositories {
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly("com.github.EterniaServer", "EterniaLib", "4.5.5")
}
```
**Gradle (Groovy):**
```groovy
repositories {
    maven {
        name "jitpack"
        url "https://jitpack.io"
    }
}

dependencies {
    compileOnly "com.github.EterniaServer:EterniaLib:4.5.5"
}
```
**Maven:**
```xml
<project>
    <repositories>
        <repository>
            <id>jitpack</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>com.github.EterniaServer</groupId>
            <artifactId>EterniaLib</artifactId>
            <version>4.5.5</version>
        </dependency>
    </dependencies>
</project>
```
### [Wiki](https://github.com/EterniaServer/EterniaLib/wiki) (Plugin Developers)


## Special Thanks

**Annotation Command Framework (ACF)**
- **https://github.com/aikar/commands**
- Thanks for creating such a complete command framework.
