# KeycloakMe

keycloakMe allows you to start a **keycloak server** in **HTTP/HTTPS mode**

Also provides a client that automatically creates Realm, Client, Users, Roles, and affects Roles to the Users 

----------------------------------------------------------

 **Usage** :

 1- Change directory : 
 
     cd KeycloakMe/script_version/

 2- Download the keyclaok server ( 4.0.6-Final ) : 
 
     wget https://downloads.jboss.org/keycloak/4.6.0.Final/keycloak-4.6.0.Final.zip 
 
 3- Unzip **keycloak-4.6.0.Final.zip** :
 
     unzip keycloak-4.6.0.Final.zip
 
 4- rename the directory **keycloak-4.6.0.Final** to **keycloak-4.6.0** :
 
     mv keycloak-4.6.0.Final keycloak-4.6.0 & keycloak-4.6.0.Final.zip
     
 5- Generte a **self-signed Certificate** ( Optional ) : 
 
    ./certificate_generator.sh
 
 6- Start keycloak server :
 
   * In **HTTP** mode  :
 
         ./keycloak_starter.sh http
         
         Goto : http://localhost:8180/ 
 
   * In **HTTPS** mode : 
 
         ./keycloak_starter.sh https
 
         Goto : https://localhost:8543
 
 7- Create Initial Admin User ( with Login/Password : admin/admin ) :
  
        ./keycloak_client.sh adduser admin admin
  
 8- Restart the keycloak server :
  
        Ctrl+C
       
        HTTP Mode : ./keycloak_starter.sh http 
        
        HTTPS Mode : ./keycloak_starter.sh https 
  

 9- Create Realm, Client, Users, Roles.. admin/admin is Login/Password of admin user ( Master Realm ) 
  
   * IF **HTTP** MODE :
   
         ./keycloak_client.sh http admin admin 
   
   * IF **HTTPS** MODE :
   
         ./keycloak_client.sh https admin admin
         
 - **This Creates :**   
 
        REALM            : my_realm
        CLIENT_ID        : my_app
        CLIENT_SECRET_ID :11111111-1111-1111-1111-111111111111
        USER_1           : with login admin  / password admin
        USER_2           : with login public / password public
        ROLE             : manager
        
        Affect the Role "Manager" to the Client "my_app" 
        Affect the Role "Manager" to the user Admin 

