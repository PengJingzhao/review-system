# 使用 OpenJDK 11 基础镜像
FROM openjdk:17-jdk-slim

ARG JAR_FILE
ARG AGENT_FILE

# 设置工作目录
WORKDIR /app

# 复制构建好的点评服务 JAR 文件
COPY ${JAR_FILE} app.jar


# 暴露 Dubbo 默认端口
EXPOSE 15003

# 默认配置文件路径（可在运行时覆盖）
ENV SPRING_CONFIG_FILE="prod"
ENV SKYWALKING_AGENT_SERVICE_NAME="review-service"
ENV SKYWALKING_COLLECTOR_BACKEND_SERVICE="10.21.32.95:11800"

# JVM 参数优化（可在运行时覆盖）
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport"
ENV AGENT_OPTS="-javaagent:/usr/local/agent/skywalking-agent.jar -Dskywalking.agent.service_name=${SKYWALKING_AGENT_SERVICE_NAME} -Dskywalking.collector.backend_service=${SKYWALKING_COLLECTOR_BACKEND_SERVICE} -Djava.security.egd=file:/dev/./urandom"

# 设置启动命令
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar --spring.profiles.active=${SPRING_CONFIG_FILE}"]
