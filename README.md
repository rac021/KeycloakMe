# KeycloakMe

keycloakMe allows you to start a **keycloak server** in **HTTP/HTTPS mode**

Also provides a client that automatically create Realm, Client, Users, Roles, and affects Roles to the Users 

----------------------------------------------------------

 **Usage** :

 1- Download the keyclaok server ( 4.0.6-Final ) : 
 
     https://downloads.jboss.org/keycloak/4.6.0.Final/keycloak-4.6.0.Final.zip
 
 
 2- Unzip **keycloak-4.6.0.Final.zip** :
 
     Then rename the directory keycloak-4.6.0.Final to keycloak-4.6.0
 
 Generte a **self-signed Certificate** : 
 
    ./certificate_generator.sh
 
 3- Start keycloak server :
 
   * In **HTTP** mode  :
 
         ./keycloak_starter.sh http
         
         Goto : http://localhost:8180/ 
 
   * In **HTTPS** mode : 
 
         ./keycloak_starter.sh https
 
         Goto : https://localhost:8543
 
  4- Create Admin User ( with Login/Password : admin/admin ) :
  
        ./keycloak_client.sh adduser admin admin
        

  5- Create Realm, Client, Users, Roles..
  
   * IF **HTTP** MODE :
   
         ./keycloak_client.sh http admin admin   
   
   * IF **HTTPS** MODE :
   
         ./keycloak_client.sh https admin admin
         
