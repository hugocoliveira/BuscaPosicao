import java.util.Properties

// Lê local.properties para obter credenciais de assinatura (não commitado no git)
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace   = "br.com.lit.busca.posicao"
    compileSdk  = 36

    defaultConfig {
        applicationId  = "br.com.lit.busca.posicao"
        minSdk         = 24
        targetSdk      = 36
        versionCode    = 8
        versionName    = "1.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Credenciais SAP — lidas do local.properties (gitignored), nunca expostas no código-fonte
        buildConfigField("String", "SAP_USERNAME", "\"${localProps.getProperty("sap.username", "")}\"")
        buildConfigField("String", "SAP_PASSWORD", "\"${localProps.getProperty("sap.password", "")}\"")
        ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a") }
    }

    // Assinatura release — valores vêm do local.properties, nunca hardcoded no git
    signingConfigs {
        create("release") {
            storeFile     = file(localProps.getProperty("keystore.path", ""))
            storePassword = localProps.getProperty("keystore.password", "")
            keyAlias      = localProps.getProperty("key.alias", "")
            keyPassword   = localProps.getProperty("key.password", "")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose      = true
        buildConfig  = true
    }
}

dependencies {
    // Kotlin + coroutines
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.android)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose BOM — todas as versões Compose saem daqui
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Rede (Retrofit + OkHttp + Gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)

    // CameraX
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // ML Kit — leitura de QR code e código de barras
    implementation(libs.mlkit.barcode.scanning)

    // Permissões Compose
    implementation(libs.accompanist.permissions)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Testes
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
