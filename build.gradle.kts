// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
//        maven("https://maven.aliyun.com/repository/central")
//        maven("https://maven.aliyun.com/repository/jcenter")
//        maven("https://maven.aliyun.com/repository/google")
        mavenCentral()
        jcenter()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha12")
        classpath(kotlin("gradle-plugin", version = "1.4.10"))

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
//        maven("https://maven.aliyun.com/repository/central")
//        maven("https://maven.aliyun.com/repository/jcenter")
//        maven("https://maven.aliyun.com/repository/google")
        mavenCentral()
        jcenter()
        google()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}