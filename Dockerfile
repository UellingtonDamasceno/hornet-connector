FROM ubuntu:bionic as build
LABEL maintainder="UDamasceno <udamasceno@ecomp.uefs.br>"

WORKDIR /opt

RUN apt-get update -y && apt-get upgrade -y && apt-get autoremove -y\ 
  && apt-get install wget -y\
  && wget https://github.com/UellingtonDamasceno/hornet-connector/archive/refs/tags/latest.zip\
  && apt-get install unzip -y\
  && unzip latest.zip\
  && rm latest.zip\
  && mv hornet-connector-latest hornet-connector\
  && cd hornet-connector\
  && apt-get install openjdk-11-jdk maven -y\
  && mvn clean install\
  && apt-get purge maven -y\
  && apt-get autoremove -y\
  && apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

FROM adoptopenjdk/openjdk11:jre-11.0.18_10-alpine as hornet-connector-run
RUN apk add --update --no-cache \
  bash \
  tcpdump \
  iperf \
  busybox-extras \
  iproute2 \
  iputils

WORKDIR /opt

COPY --from=build /opt/hornet-connector/target/hornet-connector-1.0-jar-with-dependencies.jar hornet-connector.jar
ENTRYPOINT ["java", "-jar", "hornet-connector.jar"]