plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.app.marketpal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.marketpal"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.github.bumptech.glide:glide:5.0.0-rc01")
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    implementation("com.google.android.gms:play-services-ads:23.3.0")
    implementation("androidx.concurrent:concurrent-futures:1.2.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    implementation("com.android.billingclient:billing:7.0.0")
    implementation("com.romainpiel.shimmer:library:1.4.0@aar")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.recyclerview:recyclerview-selection:1.2.0-alpha01")
    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    implementation("io.github.afreakyelf:Pdf-Viewer:2.1.1")
}