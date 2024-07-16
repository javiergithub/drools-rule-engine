plugins {
    alias(libs.plugins.springboot)
    alias(libs.plugins.springboot.dependency.management)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    kotlin("plugin.jpa") version "1.9.24"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.springboot.webflux.starter)

    implementation(libs.bundles.kotlin.all)
    implementation(libs.bundles.drools.all)
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.batch:spring-batch-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.bundles.test.all)
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = libs.versions.jvmTarget.get()
        }
    }

    test {
        useJUnitPlatform()
    }
}

spotless {
    kotlin {
        target(
            fileTree(projectDir) {
                include("**/*.kt")
                exclude("**/.gradle/**")
            }
        )
        // see https://github.com/shyiko/ktlint#standard-rules
        ktlint()
    }

    kotlinGradle {
        target(
            fileTree(projectDir) {
                include("**/*.kt")
                exclude("**/.gradle/**")
            }
        )
        // see https://github.com/shyiko/ktlint#standard-rules
        ktlint()
    }
}

val tasksDependencies = mapOf(
    "spotlessKotlinGradle" to listOf("spotlessKotlin"),
    "spotlessKotlin" to listOf("compileKotlin", "processResources", "compileTestKotlin"),
    "test" to listOf("spotlessKotlinGradle")
)

tasksDependencies.forEach { (task, dependencies) ->
    dependencies.forEach { dependsOn ->
        tasks.findByName(task)!!.mustRunAfter(dependsOn)
    }
}
