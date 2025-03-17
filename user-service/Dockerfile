# 使用 OpenJDK 11 基础镜像
FROM openjdk:11-jdk-slim

ARG JAR_FILE

# 设置工作目录
WORKDIR /app

# 复制构建好的点评服务 JAR 文件
COPY ${JAR_FILE} app.jar

# 暴露 Dubbo 默认端口
EXPOSE 21001

# 默认配置文件路径（可在运行时覆盖）
ENV SPRING_CONFIG_FILE="prod"

# JVM 参数优化（可在运行时覆盖）
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport"

# 设置启动命令
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar --spring.profiles.active=${SPRING_CONFIG_FILE}"]
