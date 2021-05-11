plugins {
    id("com.android.library")
    kotlin("android")
}
val kotlinVersion = "1.5.0"
android {
    compileSdk = 30
    defaultConfig {
        minSdk = 21
        targetSdk = 30
//        versionCode = 1
//        versionName = "0.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions.jvmTarget = "11"
    sourceSets["main"].java.srcDir("src/main/kotlin")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.core:core-ktx:1.6.0-alpha03")
    implementation("androidx.appcompat:appcompat:1.3.0-rc01")
    implementation("com.google.android.material:material:1.4.0-beta01")
}