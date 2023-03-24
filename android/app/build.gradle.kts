@file:Suppress("UnstableApiUsage")

val httpBaseUrl: String by project
val wsBaseUrl: String by project

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    id("com.google.gms.google-services")
}

android {
    namespace = "bagus2x.sosmed"
    compileSdk = 33

    defaultConfig {
        applicationId = "bagus2x.sosmed"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            // Don"t package arm64-v8a or x86_64
            abiFilters.add("armeabi-v7a")
            abiFilters.add("x86")
        }
    }
    buildTypes {
        named("debug") {
            buildConfigField("String", "HTTP_BASE_URL", """"$httpBaseUrl"""")
            buildConfigField("String", "WS_BASE_URL", """"$wsBaseUrl"""")
        }
        named("release") {
            buildConfigField("String", "HTTP_BASE_URL", """"$httpBaseUrl"""")
            buildConfigField("String", "WS_BASE_URL", """"$wsBaseUrl"""")
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
                )
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xcontext-receivers",
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/*"
        }
    }
}

dependencies {
    val composeUiVersion = "1.4.0"
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-messaging-ktx:23.1.2")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.2")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation("androidx.compose.ui:ui:$composeUiVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.0")
    implementation("androidx.compose.material:material:1.5.0-alpha01")
    implementation("androidx.compose.ui:ui-text-google-fonts:$composeUiVersion")

    val lifecycleVersion = "2.6.0"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")

    implementation("androidx.browser:browser:1.5.0")

    implementation("androidx.compose.ui:ui-util:$composeUiVersion")

    val navVersion = "2.5.3"
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.45")
    kapt("com.google.dagger:hilt-compiler:2.45")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Room
    val roomVersion = "2.5.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")

    implementation("org.jsoup:jsoup:1.15.4")

    val pagingVersion = "3.1.1"
    implementation("androidx.paging:paging-runtime:$pagingVersion")
    implementation("androidx.paging:paging-compose:1.0.0-alpha18")

    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("io.coil-kt:coil-video:2.2.2")

    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha09")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")

    // Exo Player
    implementation("androidx.media3:media3-exoplayer:1.0.0")
    implementation("androidx.media3:media3-ui:1.0.0")

    // Accompanist
    implementation("com.google.accompanist:accompanist-permissions:0.29.2-rc")
    implementation("com.google.accompanist:accompanist-navigation-material:0.29.2-rc")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.29.2-rc")
    implementation("com.google.accompanist:accompanist-placeholder-material:0.29.2-rc")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.29.2-rc")

    // Camera X
    implementation("androidx.camera:camera-camera2:1.2.2")
    implementation("androidx.camera:camera-lifecycle:1.2.2")
    implementation("androidx.camera:camera-view:1.2.2")

    implementation("androidx.palette:palette:1.0.0")

    implementation("com.google.mlkit:translate:17.0.1")
    implementation("com.google.mlkit:language-id:17.0.4")

    implementation("com.github.SmartToolFactory:Compose-Cropper:0.2.4")

    implementation("androidx.core:core-splashscreen:1.0.0")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Ktor
    val ktorVersion = "2.2.4"
    implementation("io.ktor:ktor-client-core:2.2.4")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:2.2.4")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    implementation("id.zelory:compressor:3.0.1")

    implementation("com.google.mlkit:playstore-dynamic-feature-support:16.0.0-beta2")

    implementation("org.slf4j:slf4j-android:1.7.36")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeUiVersion")

    debugImplementation("androidx.compose.ui:ui-tooling:$composeUiVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeUiVersion")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")
}
