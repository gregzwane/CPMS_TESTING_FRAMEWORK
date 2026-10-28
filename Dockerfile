# Dockerfile for CPMS Testing Framework
# Multi-stage build for efficient CI/CD

# Stage 1: Build stage with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the project and copy dependencies for the runtime image
RUN mvn clean package -DskipTests -B \
    && mvn dependency:copy-dependencies -DincludeScope=test -DoutputDirectory=target/dependency -B

# Stage 2: Test runtime stage
FROM eclipse-temurin:17-jre-jammy

# Add Maven from the builder image because the test services invoke `mvn test`
COPY --from=builder /usr/share/maven /usr/share/maven
COPY --from=builder /usr/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME=/usr/share/maven
ENV PATH="${MAVEN_HOME}/bin:${PATH}"

# Set shell options for pipefail
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# Install Chrome and dependencies for Selenium
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget=1.21.3-1ubuntu1 \
    gnupg=2.2.40-1.1ubuntu1 \
    unzip=6.0-28ubuntu1 \
    curl=7.81.0-1ubuntu1.18 \
    xvfb=2:21.1.4-2ubuntu2 \
    libxi6=2:1.8-1ubuntu1 \
    libgconf-2-4=3.2.6-6ubuntu1 \
    libxss1=1:1.2.3-1ubuntu0.22.04.1 \
    libxtst6=2:1.2.3-1ubuntu0.22.04.1 \
    libxrandr2=2:1.5.2-2ubuntu1 \
    libasound2=1.2.6.1-1ubuntu1 \
    libpangocairo-1.0-0=1.50.7+ds1-1ubuntu1 \
    libatk1.0-0=2.46.0-2ubuntu1 \
    libatk-bridge2.0-0=2.46.3-1ubuntu1 \
    libgtk-3-0=3.24.33-2ubuntu2 \
    libnss3=2:3.79-1ubuntu1 \
    libxcomposite1=1:0.4.5-2ubuntu1 \
    libxdamage1=1:1.1.6-1ubuntu0.22.04.1 \
    libxfixes3=1:6.0.0-2ubuntu1 \
    libcups2=2.4.1op1-1ubuntu4.3 \
    libdrm2=2.4.110-1ubuntu1 \
    libxkbcommon0=1.3.1-2ubuntu1 \
    libxshmfence1=1.3-2ubuntu1 \
    fonts-liberation=1:2.1.5-1ubuntu1 \
    libappindicator3-1=12.10.1+20.04.20200203-1ubuntu4 \
    lsb-release=11.1.0ubuntu3 \
    xdg-utils=1.1.3-4ubuntu2 \
    && rm -rf /var/lib/apt/lists/*

# Install current Google Chrome and Firefox packages from their configured repositories
RUN mkdir -p /etc/apt/keyrings \
    && wget -q -O /etc/apt/keyrings/google-chrome.asc https://dl.google.com/linux/linux_signing_key.pub \
    && echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google-chrome.asc] https://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends google-chrome-stable=120.0.6099.129-1 firefox=121.0.1+build1-0ubuntu0.22.04.1 \
    && rm -rf /var/lib/apt/lists/*

# Install Edge (pinned version)
RUN apt-get update && apt-get install -y --no-install-recommends wget=1.21.3-1ubuntu1 gpg=2.2.40-1.1ubuntu1 \
    && wget -q -O - https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > /usr/share/keyrings/microsoft-edge.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/microsoft-edge.gpg] https://packages.microsoft.com/repos/edge stable main" > /etc/apt/sources.list.d/microsoft-edge.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends microsoft-edge-stable=120.0.2210.133-1 \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy the Maven project and build output to their standard locations
COPY --from=builder /app/pom.xml ./pom.xml
COPY --from=builder /app/src ./src
COPY --from=builder /app/target ./target
COPY --from=builder /root/.m2/repository /opt/maven-repository

# Copy Allure commandline for report generation
RUN wget -q "https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/3.17.0/allure-commandline-3.17.0.tgz" \
    && tar -xzf allure-commandline-3.17.0.tgz \
    && mv allure-3.17.0 /opt/allure \
    && ln -s /opt/allure/bin/allure /usr/local/bin/allure \
    && rm allure-commandline-3.17.0.tgz

# Set environment variables
ENV DISPLAY=:99
ENV CHROME_BIN=/usr/bin/google-chrome
ENV CHROMEDRIVER_PATH=/usr/local/bin/chromedriver
ENV GECKO_DRIVER_PATH=/usr/local/bin/geckodriver
ENV EDGE_DRIVER_PATH=/usr/local/bin/msedgedriver

# Create non-root user for security
RUN useradd -m -s /bin/bash testuser \
    && mkdir -p /home/testuser/.m2 \
    && cp -a /opt/maven-repository /home/testuser/.m2/repository \
    && chown -R testuser:testuser /app /home/testuser/.m2
USER testuser

# Default command runs smoke tests (can be overridden)
ENTRYPOINT ["mvn", "test", "-Dheadless=true"]
CMD ["-Dtest=**/AuthenticationTests.java"]