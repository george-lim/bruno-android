import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.secrets.gradle.plugin)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.download)
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}

val hasUploadKeystore = rootProject.file("upload-keystore.jks").exists()

android {
    namespace = "com.bruno.android"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.bruno.android"
        minSdk = 33
        targetSdk = 36
        versionCode = 6
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SPOTIFY_REDIRECT_URI", "\"bruno://callback\"")
        manifestPlaceholders["redirectHostName"] = "callback"
        manifestPlaceholders["redirectPathPattern"] = "/.*"
        manifestPlaceholders["redirectSchemeName"] = "bruno"
    }

    if (hasUploadKeystore) {
        signingConfigs {
            create("release") {
                var secretsFile = rootProject.file(secrets.propertiesFileName)

                if (!secretsFile.exists()) {
                    secretsFile = rootProject.file(secrets.defaultPropertiesFileName as String)
                }

                val secretProperties = Properties().apply {
                    load(FileInputStream(secretsFile))
                }

                storeFile = rootProject.file("upload-keystore.jks")
                storePassword = secretProperties.getProperty("UPLOAD_KEYSTORE_PASSWORD")
                keyAlias = secretProperties.getProperty("UPLOAD_KEY_ALIAS")
                keyPassword = secretProperties.getProperty("UPLOAD_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            if (hasUploadKeystore) {
                signingConfig = signingConfigs["release"]
            }

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }

    room {
        schemaDirectory("schemas")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.play.services.location)
    implementation(libs.android.maps.utils)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.preference)
    implementation(libs.volley)
    implementation(libs.spotify.android.auth)
    implementation(files("libs/spotify-app-remote.aar"))
    implementation(libs.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
}

download.run {
    src(libs.versions.spotifyAppRemoteUrl)
    dest("libs/spotify-app-remote.aar")
    overwrite(false)
}