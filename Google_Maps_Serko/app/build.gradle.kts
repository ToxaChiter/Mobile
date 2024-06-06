plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.google_maps_serko"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.google_maps_serko"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation ("org.jsoup:jsoup:1.14.3")

    implementation ("com.github.jd-alexander:library:1.1.0")
    implementation ("com.google.android.material:material:1.1.0")
    implementation ("com.google.maps:google-maps-services:0.17.0")
    implementation ("com.google.maps.android:android-maps-utils:2.2.0")
    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation ("com.google.android.gms:play-services-maps:17.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-places:17.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}