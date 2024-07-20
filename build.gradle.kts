plugins {
    alias(libs.plugins.springboot)
    alias(libs.plugins.springboot.dependency.management)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
//    alias(libs.plugins.kotlin.jpa)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.springboot.webflux.starter)

    implementation(libs.bundles.kotlin.all)
    implementation(libs.bundles.drools.all)
 //   implementation("org.drools:drools-commands")
    implementation("org.drools:drools-decisiontables")
    implementation("org.drools:drools-mvel")
    implementation("org.drools:drools-commands:9.44.0.Final")
    implementation("org.drools:drools-core:9.44.0.Final")
    // https://mvnrepository.com/artifact/org.kie/kie-ci
    implementation("org.kie:kie-ci:9.44.0.Final")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.duckdb:duckdb_jdbc:1.0.0")
    implementation("org.apache.commons:commons-lang3:3.4")
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
 //               include("**/*.class")
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
 //               include("**/*.class")
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
