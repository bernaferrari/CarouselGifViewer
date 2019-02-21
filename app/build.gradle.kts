import org.jetbrains.kotlin.config.KotlinCompilerVersion

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    dataBinding.isEnabled = true
}

dependencies {

    // RX
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("io.reactivex.rxjava2:rxjava:2.2.6")
    implementation("io.reactivex.rxjava2:rxkotlin:2.3.0")
    implementation("com.jakewharton.rxrelay2:rxrelay:2.1.0")


    // Glide
    implementation("com.github.bumptech.glide:glide:4.9.0")

    // RecyclerView
    implementation("com.yarolegovich:discrete-scrollview:1.4.9")

    val epoxy = "3.2.0"
    implementation("com.airbnb.android:mvrx:0.7.2")
    implementation("com.airbnb.android:epoxy:$epoxy")
    implementation("com.airbnb.android:epoxy-databinding:$epoxy")
    kapt("com.airbnb.android:epoxy-processor:$epoxy")


    // Google
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.browser:browser:1.0.0")
    implementation("com.google.android.material:material:1.0.0")

    implementation("androidx.core:core-ktx:1.0.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")


    val navVersion = "1.0.0-beta02"
    implementation("android.arch.navigation:navigation-fragment-ktx:$navVersion")
    implementation("android.arch.navigation:navigation-ui-ktx:$navVersion")
    implementation("com.afollestad.material-dialogs:core:2.0.0")


    // Logging
    implementation("com.orhanobut:logger:2.2.0")


    // UI
    implementation("me.zhanghai.android.materialprogressbar:library:1.6.1")


    // Player
    implementation("com.devbrackets.android:exomedia:4.3.0")
    implementation("com.google.android.exoplayer:extension-okhttp:2.9.5")


    // Internal
    implementation("com.squareup.okhttp3:okhttp:3.13.1")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))

    testImplementation("junit:junit:4.12")
}
