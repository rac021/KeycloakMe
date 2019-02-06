 
 #########################
 # Step : Package image ##
 #########################

 FROM openjdk:8u181-jdk-stretch

 ENV DEBIAN_FRONTEND noninteractive

 ENV DEBCONF_NOWARNINGS yes

 COPY . /app/

 RUN chmod +x /app/*.sh

 WORKDIR /app/

 RUN apt-get upgrade              && \
     apt-get update               && \
     apt-get install -y apt-utils && \
     apt-get install -y psmisc    && \
     apt-get install -y zip 

 ADD  https://downloads.jboss.org/keycloak/4.8.3.Final/keycloak-4.8.3.Final.zip /app/

 RUN unzip /app/keycloak-4.8.3.Final.zip && rm /app/keycloak-4.8.3.Final.zip

 ENTRYPOINT ["/bin/bash", "-c", "/app/keycloak_in_docker_starter.sh && tail -f /dev/null "]

 CMD []

