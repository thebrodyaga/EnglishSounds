plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
//    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_17.toString()
            }
        }
    }

     listOf(
         iosX64(),
         iosArm64(),
         iosSimulatorArm64()
     ).forEach { iosTarget ->
         iosTarget.binaries.framework {
             baseName = "ComposeApp"
             isStatic = true
         }
     }

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(libs.compose.activity)
            }
        }
        /* iosMain.dependencies {
             implementation(libs.ktor.client.darwin)
         }*/
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
//                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
//                implementation(compose.components.resources)
//                implementation(compose.components.uiToolingPreview)

//            implementation(libs.ktor.client.core)
//            implementation(libs.ktor.client.content.negotiation)
//            implementation(libs.ktor.serialization.kotlinx.json)

//            implementation(libs.kamel)
//            implementation(libs.koin.core)
//            implementation(libs.voyager.navigator)
//            implementation(libs.voyager.koin)
            }
        }
    }
}

android {
    namespace = "com.thebrodyaga.englishsounds"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.thebrodyaga.englishsounds"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionCode = libs.versions.version.code.get().toInt()
        versionName = libs.versions.version.name.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompilerVer.get()
    }
    dependencies {
//        debugImplementation(libs.androidx.compose.ui.tooling)
    }
}