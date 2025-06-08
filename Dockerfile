# 基础镜像
FROM openjdk:17-slim

# 作者
LABEL maintainer="Aseubel"

# 配置

# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
## 安装Python及系统依赖
#RUN apt-get update && apt-get install -y python3 \python3-pip libgl1-mesa-glx && rm -rf /var/lib/apt/lists/*
## 配置Python环境
#RUN ln -snf /usr/bin/python3 /usr/bin/python && pip3 install --upgrade pip -i https://pypi.tuna.tsinghua.edu.cn/simple
#RUN pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
# apt update
# apt install -y libglib2.0-0
# 添加应用
ADD target/isee-app.jar /isee-app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /isee-app.jar $PARAMS"]