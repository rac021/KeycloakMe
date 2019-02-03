#!/bin/bash

 TRANSPORT_MODE="http"
  
 stopKeycloak()    {

    echo ; echo " ## Stop Keycloak ... "; echo 
    fuser -k 8543/tcp
    fuser -k 8081/tcp

   ./keycloak-4.8.3.Final/bin/jboss-cli.sh --connect controller=localhost:10090 command=:shutdown
 
   sleep 3

 } 
 
 startKeycloak()   {
   
     echo ; echo " ## Start Keycloak ... "; echo 
   
    ./keycloak_starter.sh $TRANSPORT_MODE &
    
    sleep 10
 }  

 if [[ "$1" = "https" || "$TRANSPORT" = "https" ]] ; then
  
     TRANSPORT_MODE="https"
 fi 
 
 ## FROM DOCKER VARIABLE_ENV
 if [[ "$TRANSPORT_MODE" = "https" ]]  ; then     
     ./certificate_generator.sh
 fi
  
 echo 
 
 startKeycloak
 
 ## FROM DOCKER VARIABLE_ENV
 
 if [ "$MODE" = "DEMO" ]  ; then  
   
     echo ; echo " ## Create User in Keycloak with login/password : admin/admin " ; echo

     ./keycloak_client.sh adduser admin admin
     
     stopKeycloak # must restart Keycloak when adding user

     echo ; echo " ## re Start Keycloak ... " ; echo 

     startKeycloak
     
    ./keycloak_client.sh $TRANSPORT_MODE admin admin &
  
 fi
 
