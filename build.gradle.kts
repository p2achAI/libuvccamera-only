plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

group = (findProperty("POM_GROUP") as String?)
    ?.takeIf { it.isNotBlank() }
    ?: "com.github.p2achAI"
android {
    namespace = "com.serenegiant.uvccamera"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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

    kotlinOptions { jvmTarget = "11"}

        externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk")
        }
    }


    publishing {
        singleVariant("release") {
            withSourcesJar()
        }

    }

//    sourceSets {
//        getByName("main") {
//            jni.srcDirs()     // 빈 배열 지정 (ndkBuild 사용)
//            jniLibs.srcDirs() // 빈 배열 지정 (빌드 산출물 사용)
//        }
//    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core:1.13.1")
    implementation("androidx.annotation:annotation:1.8.0")

    // Serenegiant common (maven repo 필요)
    api("com.serenegiant:common:4.1.1") {
        exclude(group = "com.android.support", module = "support-v4")
        exclude(group = "com.android.support", module = "support-annotations")
    }
}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("mavenRelease") {
                from(components["release"])
                groupId = project.group.toString()
                artifactId = "libuvccamera"

                pom {
                    name.set("libuvccamera")
                    description.set("UVC camera library (p2ach fork)")
                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }
                }
            }

            // 필요 시 디버그도 배포
            // create<MavenPublication>("mavenDebug") {
            //     from(components["debug"])
            //     groupId = group.toString()
            //     artifactId = "libuvccamera-debug"
            //     version = version.toString()
            // }
        }

        repositories {
            mavenLocal()
        }
    }
}