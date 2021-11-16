// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val compose by extra("1.1.0-beta02")
    val ktVersion by extra("1.5.31")
    repositories {
//        maven("https://maven.aliyun.com/repository/central")
//        maven("https://maven.aliyun.com/repository/google")
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-beta03")
        classpath(kotlin("gradle-plugin", version = rootProject.extra["ktVersion"] as String))

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
//        maven("https://maven.aliyun.com/repository/central")
//        maven("https://maven.aliyun.com/repository/google")
        mavenCentral()
        google()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}