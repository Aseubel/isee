# 第一阶段：构建Java应用
FROM openjdk:21 AS java-stage
WORKDIR /app
COPY target/isee-app.jar /app/isee-app.jar

# 第二阶段：构建Python环境并添加Java应用
FROM python:3.11.10
WORKDIR /app

# 作者
LABEL maintainer="Aseubel"

# 配置

# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装Python依赖
RUN pip install --no-cache-dir requests numpy matplotlib opencv-python

# 从第一阶段复制Java应用
COPY --from=java-stage /app/isee-app.jar /app/isee-app.jar

# 设置Java运行命令
ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /app/isee-app.jar $PARAMS"]
