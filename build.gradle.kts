plugins {
	java
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	id("io.freefair.lombok") version "8.10.2"
}

group = "vn.hoidanit"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")

	// "org.springframework.boot:spring-boot-starter-oauth2-resource-server
	// là 1 thư viện cha nó có kéo theo spring-security-oauth2-jose và spring-security-oauth2-jose
	// để giải mã, xác thực chữ ký JWT, xác thực request có Bearer Token và
	// Cấu hình tự động để đọc Authorization: Bearer <token> và gọi JwtDecoder
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("com.turkraft.springfilter:jpa:3.1.7")

	implementation("org.javers:javers-spring-boot-starter-sql:7.6.3")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
