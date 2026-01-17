plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.versionCatalogUpdate)
    alias(libs.plugins.versions)
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
}
