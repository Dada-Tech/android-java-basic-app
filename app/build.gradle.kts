plugins {
    id("com.android.application")
}

android {
    signingConfigs {
        create("clasroom key") {
            storeFile = file("/Users/daviddada/Dropbox/Northeastern/Courses/CS5520 Mobile Development/keystore5520")
            storePassword = "keystore5520"
            keyPassword = "keystore5520"
            keyAlias = "classroom key"
        }
    }
    namespace = "edu.northeastern.numad23fa_daviddada"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.northeastern.numad23fa_daviddada"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("clasroom key")
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}