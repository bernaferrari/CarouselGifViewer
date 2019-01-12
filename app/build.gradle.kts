import com.android.build.gradle.ProguardFiles.getDefaultProguardFile
import org.gradle.internal.impldep.com.amazonaws.PredefinedClientConfigurations.defaultConfig
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt
import org.jetbrains.kotlin.serialization.js.DynamicTypeDeserializer.id

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-android-extensions")
}

androidExtensions {
    isExperimental = true
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.bernaferrari.carouselgifviewer"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 2
        versionName = "2.0"
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }

    dataBinding.isEnabled = true

}

dependencies {

    // RecyclerView
    implementation("com.xwray:groupie:2.3.0")
    implementation("com.xwray:groupie-kotlin-android-extensions:2.3.0")
    implementation("com.yarolegovich:discrete-scrollview:1.4.9")


    // RX
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.5")
    implementation("io.reactivex.rxjava2:rxkotlin:2.3.0")
    implementation("com.jakewharton.rxrelay2:rxrelay:2.0.0")


    // Glide
    implementation("com.github.bumptech.glide:glide:4.8.0")


    implementation("com.airbnb.android:mvrx:0.6.0")
    implementation("com.airbnb.android:epoxy:3.1.0")
    implementation("com.airbnb.android:epoxy-databinding:3.1.0")

    kapt("com.airbnb.android:epoxy-processor:3.1.0")


    // Google

    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    implementation("androidx.appcompat:appcompat:1.0.2")
//    implementation("androidx.browser:browser:1.0.0")
    implementation("com.google.android.material:material:1.0.0")

    implementation("androidx.core:core-ktx:1.0.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")


    val nav_version = "1.0.0-alpha06"
    implementation("android.arch.navigation:navigation-fragment-ktx:$nav_version")
    implementation("android.arch.navigation:navigation-ui-ktx:$nav_version")
    implementation("com.afollestad.material-dialogs:core:2.0.0-rc7")


    // Logging
    implementation("com.orhanobut:logger:2.2.0")


    // UI
    implementation("me.zhanghai.android.materialprogressbar:library:1.4.2")
    implementation("com.devbrackets.android:exomedia:4.2.3")


    // Only on About screen
    implementation("com.mikepenz:iconics-core:3.1.0@aar")
    implementation("com.mikepenz:community-material-typeface:2.0.46.1@aar")


    // Internal
    implementation("com.squareup.okhttp3:okhttp:3.12.1")
    implementation("com.google.android.exoplayer:extension-okhttp:2.8.1")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.11")

    testImplementation("junit:junit:4.12")
}
