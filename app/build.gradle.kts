import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    jacoco
}

android {
    namespace = "com.bragi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bragi"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    hilt {
        enableAggregatingTask = false
    }
}

dependencies {

    implementation(project(":blemiddleware"))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // RXJava
    implementation(libs.rxjava3)
    implementation(libs.rxandroid)

    implementation(libs.lifecycle.viewmodel.ktx)

    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Paging
    implementation(libs.androidx.paging)
    implementation(libs.androidx.paging.compose)

    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Retrofit & Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.rxjava)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.javapoet)

    // Coroutines
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Coil for image loading
    implementation(libs.coil.compose)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
//    testImplementation(libs.strikt)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.test.ui.junit4)
    androidTestImplementation(libs.test.navigation)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.test.ui.manifest)
    testImplementation(kotlin("test"))
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.androidx.core.testing)

    // Navigation

    implementation(libs.androidx.navigation.compose)
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}


tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    val javaClasses = layout.buildDirectory
        .dir("intermediates/javac/debug/classes")
        .get()
        .asFile
    val kotlinClasses = layout.buildDirectory
        .dir("tmp/kotlin-classes/debug")
        .get()
        .asFile

    val classDirs = listOf(javaClasses, kotlinClasses).map { dir ->
        fileTree(dir) {
            exclude("**/dagger/**", "**/*_Hilt_*", "**/Dagger*.*")
        }
    }
    classDirectories.setFrom(classDirs)
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("jacoco/testDebugUnitTest.exec")
    })

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}