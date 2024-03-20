plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.healthtracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.healthtracker"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
    }

}

dependencies {
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.mikhaellopez:circularprogressbar:3.1.0")
    implementation ("io.coil-kt:coil-compose:2.1.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.github.yalantis:ucrop:2.2.8")
    implementation ("com.google.code.gson:gson:2.10")
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-preferences-rxjava2:1.0.0")
    implementation ("androidx.work:work-runtime-ktx:2.7.0")

    // optional - RxJava3 support
    implementation("androidx.datastore:datastore-preferences-rxjava3:1.0.0")
    //noinspection LifecycleAnnotationProcessorWithJava8
    kapt ("androidx.lifecycle:lifecycle-compiler:2.8.0-alpha01")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation ("com.kizitonwose.calendar:view:2.0.0")
}