# KeycloakMe

keycloakMe allows you to start a **keycloak server** in **HTTP/HTTPS mode**

Also provides a client that automatically creates Realm, Client, Users, Roles, and affects Roles to the Users 

----------------------------------------------------------

 **Usage** :

 1- Change directory : 
 
     cd script_version/

 2- Download the keyclaok server ( 4.8.3-Final ) : 
 
     wget https://downloads.jboss.org/keycloak/4.8.3.Final/keycloak-4.8.3.Final.zip 
 
 3- Unzip **keycloak-4.8.3.Final.zip** :
 
     unzip keycloak-4.8.3.Final.zip

 4- Generte a **self-signed Certificate** ( Optional ) : 
 
    ./certificate_generator.sh
 
 5- Start keycloak server :
 
   * In **HTTP** mode  :
 
         ./keycloak_starter.sh http
         
         Goto : http://localhost:8180/ 
 
   * In **HTTPS** mode : 
 
         ./keycloak_starter.sh https
 
         Goto : https://localhost:8543
 
 6- Create Initial Admin User ( with Login/Password : admin/admin ) :
  
        ./keycloak_client.sh adduser admin admin
  
 7- Restart the keycloak server ( if step 6 was performed ) :
  
        Ctrl+C
       
        HTTP Mode : ./keycloak_starter.sh http 
        
        HTTPS Mode : ./keycloak_starter.sh https 
  

 8- Create Realm, Client, Users, Roles.. admin/admin is Login/Password of admin user ( Master Realm ) 
  
   * IF **HTTP** MODE :
   
         ./keycloak_client.sh http admin admin 
   
   * IF **HTTPS** MODE :
   
         ./keycloak_client.sh https admin admin
         
 - **This Creates :**   
 
        REALM            : my_realm
        CLIENT_ID        : my_app
        CLIENT_SECRET_ID : my_secret
        USER_1           : with login admin  / password admin
        USER_2           : with login public / password public
        ROLE             : manager
        
        Affect the Role "Manager" to the Client "my_app" 
        Affect the Role "Manager" to the user Admin 

---

### Using Docker ( [Dockerfile](https://github.com/rac021/KeycloakMe/blob/master/script_version/Dockerfile) ) :

   **Note :** The script used in the docker image for creating clients, realms, users and roles is [ keycloak_client.sh ]( https://github.com/rac021/KeycloakMe/blob/master/script_version/keycloak_client.sh )


   * 1 - Http Mode :

```
         docker run -d  -p 8180:8180         \
                        -e "TRANSPORT=http"  \
                        -e "MODE=DEMO"       \
                        --name keycloakme    \
                        rac021/jaxy-keycloakme
                    
         URL :  http://ip:8180 
```

   * 2 - Https Mode :

```
         docker run -d -p 8543:8543         \
                       -e "TRANSPORT=https" \
                       -e "MODE=DEMO"       \
                       --name keycloakme    \
                       rac021/jaxy-keycloakme
                    
         URL :  https://ip:8543 
```

