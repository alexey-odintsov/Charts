import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.alexey-odintsov"
val artifact = "charts"
version = "0.0.5"

kotlin {
    jvm()
    withSourcesJar(publish = true)

    androidLibrary {
        namespace = "io.github.alexey_odintsov.charts"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava() // enable java compilation support
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(
                        JvmTarget.JVM_21
                    )
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.ui)

            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlin.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.junit)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), artifact, version.toString())

    pom {
        name = "KMP Charts library"
        description = "KMP Charts library."
        inceptionYear = "2026"
        url = "https://github.com/alexey-odintsov/Charts"
        licenses {
            license {
                name = "MIT License"
            }
        }
        developers {
            developer {
                id = "alexey-odintsov"
                name = "Alexey Odintsov"
                url = "https://github.com/alexey-odintsov/"
            }
        }
        scm {
            url = "https://github.com/alexey-odintsov/Charts"
            connection = "scm:git:git://github.com/alexey-odintsov/Charts.git"
            developerConnection = "scm:git:ssh://git@github.com/alexey-odintsov/Charts.git"
        }
    }
}
