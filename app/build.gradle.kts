plugins {
    id("com.android.application")
}

android {
    namespace = "in.proz.prozusbwebcamera"
    compileSdk = 34

    defaultConfig {
        applicationId = "in.proz.prozusbwebcamera"
        minSdk = 26
        targetSdk = 34
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

   /*  implementation ("com.serenegiant:usb-common:3.0.0")
    implementation ("com.serenegiant:uvccamera:3.0.0")
    implementation ("com.serenegiant:uvccamera-common:3.0.0")
    implementation ("com.serenegiant:common:3.0.0")*/
     implementation ("androidx.camera:camera-camera2:1.4.0-beta02")
    implementation ("androidx.camera:camera-lifecycle:1.4.0-beta02")
  //  implementation ("com.github.jiangdongguo:AndroidUSBCamera:3.3.3")
   //    implementation project(":libausbc")


    //implementation ("com.jiangdg.usbcamera:libuvccamera:2.0.6") // For USB camera support


}