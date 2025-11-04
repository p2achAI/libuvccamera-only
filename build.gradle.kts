plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

// ✅ group / version 명시 (JitPack용)
group = "com.github.p2achAI"
version = "1.0.16"

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

    kotlinOptions { jvmTarget = "11" }

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
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core:1.13.1")
    implementation("androidx.annotation:annotation:1.8.0")

    // ✅ Serenegiant common (외부 Maven repo 필요)
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
                artifactId = "libuvccamera-only"
                version = project.version.toString() // ✅ 명시 추가

                pom {
                    withXml {
                        val root = asNode()
                        val deps = (root.get("dependencies") as? groovy.util.NodeList)
                            ?.firstOrNull() as? groovy.util.Node ?: return@withXml

                        deps.children().toList().forEach { child ->
                            val dep = child as? groovy.util.Node ?: return@forEach
                            val aid = (dep.get("artifactId") as? groovy.util.NodeList)?.text()?.trim()
                            if (aid == "common") {
                                // groupId 교정
                                val gidList = dep.get("groupId") as? groovy.util.NodeList
                                if (gidList != null && gidList.isNotEmpty()) {
                                    val gidNode = gidList[0] as groovy.util.Node
                                    gidNode.setValue("com.serenegiant")    // ✅ node.value 대신 setValue()
                                } else {
                                    dep.appendNode("groupId", "com.serenegiant")
                                }

                                // version 교정
                                val verList = dep.get("version") as? groovy.util.NodeList
                                if (verList != null && verList.isNotEmpty()) {
                                    val verNode = verList[0] as groovy.util.Node
                                    verNode.setValue("4.1.1")              // ✅ setValue()
                                } else {
                                    dep.appendNode("version", "4.1.1")
                                }
                            }
                        }
                    }
                }
            }
        }

        repositories {
            mavenLocal()
        }
    }
}