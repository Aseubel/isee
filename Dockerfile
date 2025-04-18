# 基础镜像
FROM openjdk:17

# 作者
LABEL maintainer="Aseubel"

# 配置

# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 安装Python环境
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    && rm -rf /var/lib/apt/lists/*
RUN pip install --no-cache-dir requests numpy matplotlib opencv-python

# 添加应用
ADD target/isee-app.jar /isee-app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /isee-app.jar $PARAMS"]