import org.codehaus.groovy.runtime.ProcessGroovyMethods

val lifecycleVersion = "2.4.0-alpha01"
val navVersion = "2.3.5"
val roomVersion = "2.4.0-alpha02"
val permissionsdispatcher = "4.8.0"
val fragmentVersion = "1.3.3"
val activityVersion = "1.3.0-alpha08"
val gitCommitCount =
    ProcessGroovyMethods.getText(ProcessGroovyMethods.execute("git rev-list HEAD --count"))
        .trim()
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}
android {
    compileSdk = 30
    defaultConfig {
        applicationId = "top.z7workbench.bjutloginapp"
        minSdk = 26
        targetSdk = 30
        versionCode = 6
        versionName = "6.0.0-rc03"
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
    implementation(kotlin("stdlib", "1.5.10"))
    // AndroidX
//    implementation("androidx.appcompat:appcompat:1.4.0-alpha01")
    implementation("androidx.annotation:annotation:1.3.0-alpha01")
    implementation("androidx.appcompat:appcompat:1.3.0-rc01")
    implementation("androidx.core:core-ktx:1.6.0-beta01")
    // Preference
    implementation("androidx.preference:preference:1.1.1")
    implementation("androidx.preference:preference-ktx:1.1.1")
    // Layouts
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0-beta02")
    implementation("androidx.recyclerview:recyclerview:1.2.0")
    implementation("androidx.recyclerview:recyclerview-selection:1.2.0-alpha01")
    implementation("com.google.android.material:material:1.4.0-beta01")
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
    // Annotation processor
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycleVersion")
    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // Paging
    implementation("androidx.paging:paging-runtime:3.0.0")
    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0-beta01")

//    // Jetpack Compose UI
//    implementation("androidx.compose.ui:ui:1.0.0-beta07")
//    // Tooling support (Previews, etc.)
//    implementation("androidx.compose.ui:ui-tooling:1.0.0-beta07")
//    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
//    implementation("androidx.compose.foundation:foundation:1.0.0-beta07")
//    // Material Design
//    implementation("androidx.compose.material:material:1.0.0-beta07")
//    // Material design icons
//    implementation("androidx.compose.material:material-icons-core:1.0.0-beta07")
//    implementation("androidx.compose.material:material-icons-extended:1.0.0-beta07")
//    // Integration with activities
//    implementation("androidx.activity:activity-compose:1.3.0-alpha08")
//    // Integration with ViewModels
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha05")
//    // Integration with observables
//    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-beta07")
//    implementation("androidx.compose.runtime:runtime-rxjava2:1.0.0-beta07")

    implementation("com.android.volley:volley:1.2.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.daimajia.numberprogressbar:library:1.4@aar")
    implementation("com.squareup.moshi:moshi:1.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.11.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.11.0")
    implementation("com.github.permissions-dispatcher:permissionsdispatcher:$permissionsdispatcher")
    kapt("com.github.permissions-dispatcher:permissionsdispatcher-processor:$permissionsdispatcher")
}
repositories {
    mavenCentral()
}