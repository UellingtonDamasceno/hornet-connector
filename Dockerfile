FROM ubuntu:bionic as build
LABEL maintainder="UDamasceno <udamasceno@ecomp.uefs.br>"

WORKDIR /opt
COPY . .

RUN apt-get update -y && apt-get upgrade -y && apt-get autoremove -y\ 
  && apt-get install openjdk-11-jdk maven -y\
  && mvn clean install\
  && apt-get purge maven -y\
  && apt-get autoremove -y\
  && apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

FROM ubuntu:bionic
RUN apt-get update -y && apt-get upgrade -y && apt-get autoremove -y\ 
  && apt-get install --no-install-recommends net-tools -y\
  && apt-get install --no-install-recommends iproute2 -y\
  && apt-get install --no-install-recommends iputils-ping -y\
  && apt-get install --no-install-recommends openjdk-11-jre -y\
  && apt-get autoremove -y\
  && apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

COPY --from=build /opt/target/honet-connector-1.0-jar-with-dependencies.jar honet-connector.jar
ENTRYPOINT ["java", "-jar", "honet-connector.jar"]