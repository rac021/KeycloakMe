#!/bin/bash

 TRANSPORT_MODE="http"
  
 killKeycloak()    {
   
   fuser -k 8180/tcp 
   fuser -k 8543/tcp
 } 
 
 startKeycloak()    {
   
    if [ "$TRANSPORT_MODE" = "https" ] ; then
  
       ./keycloak_starter.sh https
    else
       ./keycloak_starter.sh http
    fi
 
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
     
     killKeycloak # must restart Keycloak when adding user

     echo ; echo " ## re Start Keycloak ... " ; echo 

     startKeycloak

     sleep 10 

     ./keycloak_client.sh http admin admin &

 fi
 
 
