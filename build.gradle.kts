/*
 * Designed and developed by Duckie Team, 2022~2023
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/quack-quack-android/blob/master/LICENSE
 */

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

private val quackVersioningTaskTypeArgument = "type"
private val quackVersioningTaskBumpArgument = "bump"
private val quackInitializeVersion = "2.0.0-alpha01"

plugins {
    alias(libs.plugins.kotlin.detekt)
    alias(libs.plugins.kotlin.ktlint)
    alias(libs.plugins.gradle.dependency.handler.extensions)
    alias(libs.plugins.gradle.dependency.graph)
}

private val quackquackColor = "#36bcf5"
private val projectDependencyInfoMap = mapOf(
    "casa" to DependencyInfo(color = "#D4C5F9"),
    "catalog" to DependencyInfo(color = "#8ED610", isBoxShape = true),
    "aide" to DependencyInfo(color = "#98E1CF"),
    "sugar" to DependencyInfo(color = "#BFD4F2"),
    "runtime" to DependencyInfo(color = quackquackColor),
    "material" to DependencyInfo(color = quackquackColor),
    "animation" to DependencyInfo(color = quackquackColor),
    "ui" to DependencyInfo(color = quackquackColor),
)

dependencyGraphConfig {
    dotFilePath = "assets/project-dependency-graph.dot"
    outputFormat = OutputFormat.SVG

    dependencyBuilder { project ->
        val projectSimpleName = project.name.split("-").first()
        projectDependencyInfoMap[projectSimpleName]
    }
}

@Suppress("ktlint")
tasks.matching { task ->
    task.name.contains("dependencyGraph")
}.configureEach {
    notCompatibleWithConfigurationCache("https://github.com/jisungbin/dependency-graph-plugin/issues/8")
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.gradle.android)
        classpath(libs.kotlin.gradle)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    apply {
        plugin(rootProject.libs.plugins.kotlin.detekt.get().pluginId)
        plugin(rootProject.libs.plugins.kotlin.ktlint.get().pluginId)
        plugin(rootProject.libs.plugins.gradle.dependency.handler.extensions.get().pluginId)
    }

    afterEvaluate {
        extensions.configure<DetektExtension> {
            parallel = true
            buildUponDefaultConfig = true
            toolVersion = libs.versions.kotlin.detekt.get()
            config.setFrom(files("$rootDir/detekt-config.yml"))
        }

        extensions.configure<KtlintExtension> {
            version.set(rootProject.libs.versions.kotlin.ktlint.source.get())
            android.set(true)
        }

        tasks.withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-opt-in=kotlin.OptIn",
                    "-opt-in=kotlin.RequiresOptIn",
                )
            }
        }
    }

    // TODO: 로직 재구현
    // TOOD: Testing
    tasks.create("versioning") {
        if (project.parent == null) return@create

        val versionFile = File(projectDir, "version.txt")
        versionFile.writeText(quackInitializeVersion)

        /*val type = getPropertyOrNull<String, VersioningType>(quackVersioningTaskTypeArgument) { type ->
            VersioningType.values().find { enum ->
                enum.name.equals(type, ignoreCase = true)
            } ?: gradleError(
                """
                The value of the type property is invalid. (input: $type)
                Possible types: ${VersioningType.values().joinToString()}
                """.trimIndent(),
            )
        } ?: return@create
        val bump = getPropertyOrNull<String, BumpType>(quackVersioningTaskBumpArgument) { bump ->
            BumpType.values().find { enum ->
                enum.name.equals(bump, ignoreCase = true)
            } ?: gradleError(
                """
                The value of the bump property is invalid. (input: $bump)
                Possible bumps: ${BumpType.values().joinToString()}
                """.trimIndent(),
            )
        }

        val versionFile = File(projectDir, "version.txt")
        when (type) {
            VersioningType.Init -> {
                if (!versionFile.exists()) {
                    versionFile.createNewFile()
                    versionFile.writeText(quackInitializeVersion)
                }
            }
            VersioningType.Bump -> {
                checkNotNull(bump) {
                    "The `VersioningType = Bump` was entered, but no bump target was given."
                }
                checkVersionFileIsValid(versionFile)
                val newVersion = versionFile.bump(bump)
                versionFile.writeText(newVersion)
            }
        }*/
    }
}

tasks.register(name = "cleanAll", type = Delete::class) {
    allprojects.map(Project::getBuildDir).forEach(::delete)
}

enum class VersioningType {
    Init, Bump,
}

enum class BumpType {
    Major, Minor, Patch,
}

inline fun <reified P, T> Project.getPropertyOrNull(key: String, parse: (value: P) -> T): T? {
    return ((properties[key] ?: return null) as P).let(parse)
}

fun File.bump(type: BumpType): String {
    val now = readLines().first()
    val (major, minor, patch) = now.split(".").map(String::toInt)
    return when (type) {
        BumpType.Major -> "${major + 1}.$minor.$patch"
        BumpType.Minor -> "$major.${minor + 1}.$patch"
        BumpType.Patch -> "$major.$minor.${patch + 1}"
    }
}

fun checkVersionFileIsValid(file: File) {
    if (!file.exists() || !file.isFile) {
        error(
            """
            There is no version.txt file in the project path. 
            Try `./gradlew :project:versioning -Ptype=init` for version configuration.
            """.trimIndent(),
        )
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun gradleError(message: String): Nothing {
    throw GradleException(message)
}
