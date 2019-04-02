import com.bernaferrari.buildsrc.Libs

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

    implementation(project(":base"))
    implementation(project(":base-android"))


    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.Coroutines.core)
    implementation(Libs.Coroutines.android)

    // Dagger
    implementation(Libs.Dagger.dagger)
    kapt(Libs.Dagger.compiler)

    implementation(Libs.Dagger.androidSupport)
    kapt(Libs.Dagger.androidProcessor)

    compileOnly(Libs.AssistedInject.annotationDagger2)
    kapt(Libs.AssistedInject.processorDagger2)


    // RX
    implementation(Libs.RxJava.rxJava)
    implementation(Libs.RxJava.rxAndroid)
    implementation(Libs.RxJava.rxKotlin)
    implementation(Libs.RxJava.rxRelay)

    // Glide
    implementation(Libs.Glide.glide)
    kapt(Libs.Glide.compiler)

    // RecyclerView
    implementation(Libs.discreteScrollView)

    // Epoxy
    implementation(Libs.Epoxy.epoxy)
    implementation(Libs.Epoxy.dataBinding)
    kapt(Libs.Epoxy.processor)

    implementation(Libs.MvRx.main)
    testImplementation(Libs.MvRx.testing)


    // Google
    implementation(Libs.Google.material)
    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.constraintlayout)
    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.recyclerview)
    implementation(Libs.AndroidX.browser)
    implementation(Libs.AndroidX.Lifecycle.extensions)

    implementation(Libs.AndroidX.Navigation.navigationUi)
    implementation(Libs.AndroidX.Navigation.navigationFragment)

    implementation(Libs.materialDialogs)
    implementation(Libs.logger)
    implementation(Libs.okHttp)


    // UI
    implementation("me.zhanghai.android.materialprogressbar:library:1.6.1")


    // Player
    implementation("com.devbrackets.android:exomedia:4.3.0")
    implementation("com.google.android.exoplayer:extension-okhttp:2.9.5")

    testImplementation("junit:junit:4.12")
}
