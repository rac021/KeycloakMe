# KeycloakMe

keycloakMe allows you to start a **keycloak server** in **HTTP/HTTPS mode**.

----------------------------------------------------------

* **Usage** :

 1- Download the keyclaok server ( 4.0.6-Final ) : 
 
     https://downloads.jboss.org/keycloak/4.6.0.Final/keycloak-4.6.0.Final.zip
 
 
 2- Unzip **keycloak-4.6.0.Final.zip** :
 
     Then rename the directory keycloak-4.6.0.Final to keycloak-4.6.0
 
 Generte a **self-signed Certificate** : 
 
    ./certificate_generator.sh
 
 3- Start keycloak server :
 
   * In **HTTP** mode  :
 
         ./keycloak_starter.sh http
 
   * in **HTTPS** mode : 
 
         ./keycloak_starter.sh https
 
 
 
