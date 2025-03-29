# 基础镜像
FROM openjdk:21

# 作者
LABEL maintainer="Aseubel"

# 配置

# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 添加应用
ADD target/isee-app.jar /isee-app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /isee-app.jar $PARAMS"]