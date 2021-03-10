import org.codehaus.groovy.runtime.ProcessGroovyMethods

val lifecycleVersion = "2.3.0"
val navVersion = "2.3.3"
val roomVersion = "2.3.0-beta02"
val gitCommitCount = ProcessGroovyMethods.getText(ProcessGroovyMethods.execute("git rev-list HEAD --count")).trim()
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
//    kotlin("plugin.serialization") version "1.4.10"
}
android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "top.z7workbench.bjutloginapp"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 6
        versionName = "6.0.0-beta6"
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
    implementation(kotlin("stdlib", "1.4.31"))
    // AndroidX
    implementation("androidx.appcompat:appcompat:1.3.0-beta01")
    implementation("androidx.core:core-ktx:1.5.0-beta02")
    // Preference
    implementation("androidx.preference:preference:1.1.1")
    implementation("androidx.preference:preference-ktx:1.1.1")
    // Layouts
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0-alpha2")
    implementation("androidx.recyclerview:recyclerview:1.2.0-beta02")
    implementation("com.google.android.material:material:1.4.0-alpha01")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    // Lifecycles only (without ViewModel or LiveData)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    // Saved state module for ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    // Annotation processor
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycleVersion")
    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // Paging
    implementation("androidx.paging:paging-runtime:3.0.0-beta01")
    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha07")

    implementation("com.android.volley:volley:1.2.0-rc1")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    // implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation("com.daimajia.numberprogressbar:library:1.4@aar")
    implementation("com.squareup.moshi:moshi:1.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.11.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.11.0")
}
repositories {
    mavenCentral()
}