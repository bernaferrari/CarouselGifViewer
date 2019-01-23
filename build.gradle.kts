// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlin = "1.3.20"
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.0-alpha01")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}
