plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.map"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.map"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig =  true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //rx Java
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation ("io.reactivex.rxjava3:rxjava:3.1.5")
    implementation ("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    //Volley
    implementation("com.android.volley:volley:1.2.1")

//    // Klaxon library for JSON parsing
//    implementation ("com.beust:klaxon:0.30")
//    implementation ("org.jetbrains.anko:anko-sdk15:0.8.2")
    //map
    implementation ("com.google.maps.android:android-maps-utils:2.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.maps:google-maps-services:0.15.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-places:17.0.0")
    implementation ("joda-time:joda-time:2.10.14")
    implementation("com.google.android.libraries.places:places:3.3.0")
    implementation("com.google.ar.sceneform:filament-android:1.17.1")
    implementation("com.google.firebase:protolite-well-known-types:18.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}