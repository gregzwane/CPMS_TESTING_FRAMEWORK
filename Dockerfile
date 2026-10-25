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

# Build the project (skip tests for builder stage)
RUN mvn clean package -DskipTests -B

# Stage 2: Test runtime stage
FROM eclipse-temurin:17-jre-jammy

# Install Chrome and dependencies for Selenium
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    curl \
    xvfb \
    libxi6 \
    libgconf-2-4 \
    libxss1 \
    libxtst6 \
    libxrandr2 \
    libasound2 \
    libpangocairo-1.0-0 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libnss3 \
    libxcomposite1 \
    libxdamage1 \
    libxfixes3 \
    libcups2 \
    libdrm2 \
    libxkbcommon0 \
    libxshmfence1 \
    fonts-liberation \
    libappindicator3-1 \
    lsb-release \
    xdg-utils \
    && rm -rf /var/lib/apt/lists/*

# Install Google Chrome (latest stable)
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Firefox (latest stable)
RUN apt-get update && apt-get install -y firefox \
    && rm -rf /var/lib/apt/lists/*

# Install Edge (latest stable)
RUN apt-get update && apt-get install -y wget gpg \
    && wget -q -O - https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > /usr/share/keyrings/microsoft-edge.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/microsoft-edge.gpg] https://packages.microsoft.com/repos/edge stable main" > /etc/apt/sources.list.d/microsoft-edge.list \
    && apt-get update && apt-get install -y microsoft-edge-stable \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy built artifacts from builder
COPY --from=builder /app/target/*.jar ./
COPY --from=builder /app/target/test-classes ./test-classes
COPY --from=builder /app/target/classes ./classes
COPY --from=builder /app/target/dependency ./dependency

# Copy test resources and configuration
COPY src/test/resources ./src/test/resources

# Copy Allure commandline for report generation
RUN wget -q "https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/2.24.0/allure-commandline-2.24.0.tgz" \
    && tar -xzf allure-commandline-2.24.0.tgz \
    && mv allure-2.24.0 /opt/allure \
    && ln -s /opt/allure/bin/allure /usr/local/bin/allure \
    && rm allure-commandline-2.24.0.tgz

# Set environment variables
ENV DISPLAY=:99
ENV CHROME_BIN=/usr/bin/google-chrome
ENV CHROMEDRIVER_PATH=/usr/local/bin/chromedriver
ENV GECKO_DRIVER_PATH=/usr/local/bin/geckodriver
ENV EDGE_DRIVER_PATH=/usr/local/bin/msedgedriver

# Create non-root user for security
RUN useradd -m -s /bin/bash testuser \
    && chown -R testuser:testuser /app
USER testuser

# Default command runs smoke tests (can be overridden)
ENTRYPOINT ["mvn", "test", "-Dheadless=true"]
CMD ["-Dtest=**/AuthenticationTests.java"]
