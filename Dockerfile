FROM openjdk:8-jdk-alpine

ENV LANG zh_CN.UTF-8

RUN echo -e "https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/main\n\
https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/community" > /etc/apk/repositories

RUN apk --update add curl bash ttf-dejavu && \
      rm -rf /var/cache/apk/*

COPY target/lesscode-gateway.jar /data/app/lesscode-gateway/lesscode-gateway.jar
COPY bin /data/app/lesscode-gateway/bin

WORKDIR /data/app/lesscode-gateway/bin

RUN sed -i 's/bash/sh/g' *.sh \
    && echo -e "\ntail -f /dev/null" >> start.sh \
    && cat start.sh

EXPOSE 10999

CMD ["./start.sh"]