 
 #########################
 # Step : Package image ##
 #########################

 FROM debian:buster-slim

 ENV DEBIAN_FRONTEND noninteractive

 ENV DEBCONF_NOWARNINGS yes

 COPY . /app/

 WORKDIR /app/

 RUN apt-get upgrade              && \
     apt-get update               && \
     apt-get install -y apt-utils && \
     apt-get install -y psmisc

 ADD  https://downloads.jboss.org/keycloak/4.8.1.Final/keycloak-4.8.1.Final.zip /app/

 RUN tar -zxf /app/keycloak-4.8.1.Final.zip && rm /app/keycloak-4.8.1.Final.zip

 ENTRYPOINT ["/bin/bash", "-c", "/app/keycloak_starter.sh && tail -f /dev/null "]

 CMD []
