//import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")

}

android {
    namespace = "com.apicta.myoscopealert"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.apicta.myoscopealert"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
        }
        debug {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17" // or "11" / "1.8" / "17"

    }

//    kotlin {
//        jvmToolchain(17)
//    }

    buildFeatures {
        compose = true
        viewBinding = true
        mlModelBinding = true

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += listOf("/META-INF/{AL2.0,LGPL2.1}", "META-INF/INDEX.LIST", "META-INF/io.netty.versions.properties", "META-INF/DEPENDENCIES", "META-INF/LICENSE.md", "META-INF/LICENSE-notice.md")
        }
    }
//    kapt {
//        javacOptions {
//            correctErrorTypes = true
//            option("-J--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
//        }
//    }

}

dependencies {
    /*WAV Feature Extraction*/
//    implementation("com.github.wendykierp:JTransforms:3.1")
//    implementation("org.apache.commons:commons-math3:3.6.1")
//    implementation("com.github.wendux:Wavelet:1.0.1") // Untuk Wavelet Transform
//    implementation("com.github.JorenSix:TarsosDSP:2.4")
//    implementation("be.tarsos.dsp:core:2.5")
//    implementation("be.tarsos.dsp:jvm:2.5")
    // Audio processing
//    implementation("com.github.psambit9791:jdsp:3.0.0")
    implementation("com.github.psambit9791:jdsp:0.4.0") {
        exclude(group = "org.apache.maven.surefire", module = "common-java5")
        exclude(group = "org.apache.maven.surefire", module = "surefire-api")
    }

    implementation("org.apache.commons:commons-math3:3.6.1")

    // Fast Fourier Transform
    implementation("com.github.wendykierp:JTransforms:3.1")



    /*TFLITE MODEL*/
    implementation("org.tensorflow:tensorflow-lite:2.8.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.3.1")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.3.0")

    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")

    implementation("androidx.appcompat:appcompat:1.6.1")

    /*BLUETOOTH*/
    implementation("com.github.prasad-psp:Android-Bluetooth-Library:1.0.2")
//    implementation("com.github.prasad-psp:Android-Bluetooth-Library:1.0.4")

    
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    implementation("com.google.accompanist:accompanist-permissions:0.30.1")


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")


    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

//    viewbinding
    implementation("androidx.compose.ui:ui-viewbinding:1.2.0")

    implementation("com.google.android.material:material:1.4.0")

//    extended icon
    implementation("androidx.compose.material:material-icons-extended:1.4.3")

//    HILT
    implementation("com.google.dagger:hilt-android:2.44")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(files("C:\\Users\\kesain\\Downloads\\TarsosDSP-2.4.jar"))
    implementation(files("C:\\Users\\kesain\\Downloads\\jwave-1.0.0.jar"))


    kapt("com.google.dagger:hilt-android-compiler:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    //RETROFIT
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")

    // Preferences DataStore (SharedPreferences like APIs)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Compose
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    //Splash Api
    implementation("androidx.core:core-splashscreen:1.0.1")

    val lottieVersion = "6.1.0"
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")

    // Media Player
    val media3_version = "1.1.0"
    implementation("androidx.media3:media3-datasource-okhttp:$media3_version")
    implementation("androidx.media3:media3-exoplayer:$media3_version")
    implementation("androidx.media3:media3-ui:$media3_version")
    implementation("androidx.media3:media3-session:$media3_version")
    implementation("androidx.legacy:legacy-support-v4:1.0.0") // Needed MediaSessionCompat.Token

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation ("com.google.accompanist:accompanist-permissions:0.30.0")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
