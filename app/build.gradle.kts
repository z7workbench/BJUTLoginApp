import org.codehaus.groovy.runtime.ProcessGroovyMethods

val lifecycleVersion = "2.4.0-alpha02"
val navVersion = "2.3.5"
val roomVersion = "2.4.0-alpha03"
val fragmentVersion = "1.3.5"
val activityVersion = "1.3.0-beta02"
val gitCommitCount =
    ProcessGroovyMethods.getText(ProcessGroovyMethods.execute("git rev-list HEAD --count"))
        .trim()
plugins {
    id("com.android.application")
    // Add ksp
    id("com.google.devtools.ksp") version "1.5.30-1.0.0-beta08"
    kotlin("android")
//    kotlin("kapt")
}
android {
    compileSdk = 31
    defaultConfig {
        applicationId = "top.z7workbench.bjutloginapp"
        minSdk = 26
        targetSdk = 30
        versionCode = 6
        versionName = "6.0.0-rc04"
        versionNameSuffix = " (${gitCommitCount})"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("github") {
            storeFile = file("../z7-login-keystore.jks")
            storePassword = System.getenv("store_psd")
            keyAlias = System.getenv("key_alias")
            keyPassword = System.getenv("key_psd")
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("github")
        }
    }
    sourceSets["main"].java.srcDir("src/main/kotlin")
    kotlinOptions.jvmTarget = "11"
    buildFeatures.viewBinding = true
}

dependencies {
    implementation(project(":library"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib", "1.5.30"))
    // AndroidX
    implementation("androidx.appcompat:appcompat:1.4.0-alpha03")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.core:core-splashscreen:1.0.0-alpha01")
    // Preference
    implementation("androidx.preference:preference:1.1.1")
    implementation("androidx.preference:preference-ktx:1.1.1")
    // Layouts
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.material:material:1.4.0")
    // Activity
    implementation("androidx.activity:activity:$activityVersion")
    implementation("androidx.activity:activity-ktx:$activityVersion")
    // Fragment
    implementation("androidx.fragment:fragment:$fragmentVersion")
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    // Lifecycles only (without ViewModel or LiveData)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    // Saved state module for ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // Paging
    implementation("androidx.paging:paging-runtime:3.0.1")
    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("com.android.volley:volley:1.2.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.daimajia.numberprogressbar:library:1.4@aar")
    implementation("io.coil-kt:coil:1.3.2")
}
repositories {
    mavenCentral()
}