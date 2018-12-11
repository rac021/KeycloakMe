# KeycloakMe

keycloakMe is a java project that allows you to create/remove Realms, Clients, Users, Roles, and affects Roles to the clients and Users juste by providing a simple file configuration. 

----------------------------------------------------------

 ## **Usage** :

 1- **Install** : 
 
     mvn clean package
 
 
 2- **Usage ( run the jar by providing the file configuration )**
 
     java -jar target/keycloakMe.jar $(pwd)/keycloak.txt

 
 3- **Take a look at the script version ( for launching keycloak in HTTPS mode, creates/remove Realms, clients.. )**

   *  [**keycloakMe Script Version**]( https://github.com/rac021/KeycloakMe/tree/master/script_version )
