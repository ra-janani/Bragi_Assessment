plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    jacoco
}

android {
    namespace = "com.bragi.blemiddleware"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35

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
    hilt {
        enableAggregatingTask = false
    }
}

dependencies {

    // RXJava
    implementation(libs.rxjava3)
    implementation(libs.rxandroid)
    implementation(libs.rxandroid.ble)

    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(libs.rxandroid.ble.test)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
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
            exclude("**/dagger/**", "**/*_Hilt_*", "**/Dagger*.*", "**/di*.*")
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