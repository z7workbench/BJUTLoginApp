import org.codehaus.groovy.runtime.ProcessGroovyMethods
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

val gitCommitCount= ProcessGroovyMethods.getText(ProcessGroovyMethods.execute("git rev-list HEAD --count")).trim()
android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "xin.z7workbench.bjutloginapp"
        minSdkVersion(26)
        targetSdkVersion(29)
        versionCode = 5
        versionName = "5.0.3"
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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk7", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION))
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.0-alpha1") {
        exclude("com.android.support", "support-annotations")
    }
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.core:core-ktx:1.2.0")
    implementation("androidx.preference:preference:1.1.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0-beta01")
    implementation("com.google.android.material:material:1.2.0-alpha05")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("com.github.medyo:fancybuttons:1.8.4")
    implementation("com.squareup.okhttp3:okhttp:4.3.1")
    implementation("com.squareup.retrofit2:retrofit:2.4.0")
    implementation("com.timqi.sectorprogressview:library:2.0.1")
    implementation("com.github.jorgecastilloprz:fabprogresscircle:1.01@aar")
    implementation("com.daimajia.numberprogressbar:library:1.4@aar")
    implementation("com.airbnb.android:lottie:3.3.1")
    testImplementation("junit:junit:4.12")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.jrummyapps:colorpicker:2.1.7")
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    kapt("androidx.lifecycle:lifecycle-common-java8:2.2.0")
    // Room
    implementation("androidx.room:room-runtime:2.2.4")
    kapt("androidx.room:room-compiler:2.2.4")
    // Paging
    implementation("androidx.paging:paging-runtime:2.1.1")
}
repositories {
    mavenCentral()
}