import org.codehaus.groovy.runtime.ProcessGroovyMethods

val lifecycleVersion = "2.2.0"
val navVersion = "2.3.0"
val gitCommitCount = ProcessGroovyMethods.getText(ProcessGroovyMethods.execute("git rev-list HEAD --count")).trim()
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.4.0"
}
android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "xin.z7workbench.bjutloginapp"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 6
        versionName = "6.0.0-alpha7"
        versionNameSuffix = " (${gitCommitCount})"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("github") {
            storeFile = file("../zero-own-keystore.jks")
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions.jvmTarget = "1.8"
    buildFeatures.viewBinding = true
}

dependencies {
    implementation(project(":library"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib", "1.4.0"))
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.0-alpha1") {
        exclude("com.android.support", "support-annotations")
    }
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.preference:preference:1.1.1")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")
    implementation("com.daimajia.numberprogressbar:library:1.4@aar")
    implementation("com.google.android.material:material:1.3.0-alpha02")
    implementation("com.github.jorgecastilloprz:fabprogresscircle:1.01@aar")
    testImplementation("junit:junit:4.13")
    implementation("com.squareup.okhttp3:okhttp:4.3.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.5.0")
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
    implementation("androidx.room:room-runtime:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5")
    // Paging
    implementation("androidx.paging:paging-runtime:2.1.2")

    // Java language implementation
    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")

    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")
}
repositories {
    mavenCentral()
}