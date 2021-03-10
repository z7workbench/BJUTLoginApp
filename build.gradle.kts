// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
//        maven("https://maven.aliyun.com/repository/central")
//        maven("https://maven.aliyun.com/repository/google")
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha08")
        classpath(kotlin("gradle-plugin", version = "1.4.31"))

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