plugins {
    id("com.android.library")
    id("maven-publish")
}

group = project.findProperty("group") as String? ?: "com.github.p2achAI"
version = project.findProperty("version") as String? ?: "1.0.0"

android {
    namespace = "com.serenegiant.uvccamera"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
        consumerProguardFiles("proguard-rules.pro")
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

    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk")
        }
    }

    // ✅ 먼저 variant를 공개해 두어야 components["release"]가 생성됩니다.
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
        // 필요하면 debug도 공개
        // singleVariant("debug") { withSourcesJar() }
    }

    sourceSets {
        getByName("main") {
            jni.srcDirs()     // 빈 배열 지정 (ndkBuild 사용)
            jniLibs.srcDirs() // 빈 배열 지정 (빌드 산출물 사용)
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core:1.13.1")
    implementation("androidx.annotation:annotation:1.8.0")

    // Serenegiant common (maven repo 필요)
    implementation("com.serenegiant:common:4.1.1") {
        exclude(group = "com.android.support", module = "support-v4")
        exclude(group = "com.android.support", module = "support-annotations")
    }
}

// ❗️ from(components["release"])는 afterEvaluate 시점에서!
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("mavenRelease") {
                from(components["release"])
                groupId = group.toString()
                artifactId = "libuvccamera"
                version = version.toString()

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