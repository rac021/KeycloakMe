#!/bin/bash

 KEYCLOAK_DIRECTORY_INSTALLATION="keycloak-4.8.3.Final"
 
 if [ ! -d $(pwd)/$KEYCLOAK_DIRECTORY_INSTALLATION ]  ; then
 
    echo; 
    echo " No directory [[ $KEYCLOAK_DIRECTORY_INSTALLATION ]] Found !! "
    echo " keycloak has to be installed in a directory named [[ $KEYCLOAK_DIRECTORY_INSTALLATION ]] " ; echo 
    exit   
 fi
  
 current_script_name=`basename "$0"`
 
 echo 
 echo " ===================================================================================="
 echo " To run keycloak in HTTP  mode just run the command : ./$current_script_name http"
 echo " To run keycloak in HTTPS mode just run the command : ./$current_script_name https"
 echo " ===================================================================================="
 echo 
  
 export JBOSS_HOME=$(pwd)/$KEYCLOAK_DIRECTORY_INSTALLATION

 JKS_FILE_NAME="keystore.jks"
 
 PATH_CONF="$KEYCLOAK_DIRECTORY_INSTALLATION/standalone/configuration"
 
 ## IMPORTANT 
 # >> https://docs.jboss.org/jbossweb/7.0.x/ssl-howto.html  << 
 # Note: your private key password and keystore password should be the same. If they differ, 
 # you will get an error along the lines of java.io.IOException: Cannot recover key, 
 # as documented in Bugzilla issue 38217, which contains further references for this issue
 
 KEYSTORE_PASSWORD="my_super_password_007"
 
 KEY_PASSWORD="my_super_password_007"
 
  
 if [ ! -d $PATH_CONF/transport  ]  ; then
 
    echo; echo " Installing the HTTP/HTTPS Configuration..." ; echo 
    cp -r keycloak_4_conf/transport $PATH_CONF
   
 fi

 ## FROM DOCKER VARIABLE_ENV
 if [[ "$TRANSPORT" = "https" ]]  ; then     
     ./certificate_generator.sh
 fi
 
 if [[ "$1" = "https" &&  ! -f $JKS_FILE_NAME  ]]  ; then  
   
     echo
     echo -e "\e[91m Missing $JKS_FILE_NAME ! \e[39m "  
     echo -e " Please run the script [[ certificate_generator.sh ]] to generate a keystore.jks certificate"
     echo
     exit      
 fi
 
 echo 
 
   
 if [[ "$1" = "https"  ]]      ; then
  
   if [ -f "$PATH_CONF/$JKS_FILE_NAME" ]  ; then
   
       rm $PATH_CONF/$JKS_FILE_NAME
   fi
 
   cp $JKS_FILE_NAME $PATH_CONF/$JKS_FILE_NAME
 
 fi 
 
 defaultConf="$PATH_CONF/standalone.xml"

 httpSource="$PATH_CONF/transport/standalone_HTTP.xml"
 httpDest="$PATH_CONF/standalone_HTTP.xml"
 
 httpsSource="$PATH_CONF/transport/standalone_HTTPS.xml"
 httpsDest="$PATH_CONF/standalone_HTTPS.xml"
 
 
 if [ "$1" = "https" ] ; then 
 
    cp $httpsSource $httpsDest 
    toRename=$httpsDest 
    echo " STARTING IN -- HTTPS -- MODE "
    
 else 
 
    cp $httpSource $httpDest
    toRename=$httpDest
    echo " STARTING IN -- HTTP -- MODE "
    
 fi

 ## Delete the default Configuration if exists !
 
 if [ -f $defaultConf ]  ; then
 
    rm -f $defaultConf
 fi

 # Rename the Destination to standalone.xml
 mv $toRename $defaultConf
 
 
 ## HTTPS - UPDATE KEY_PASSWORD and KEYSTORE_PASSWORD
 sed -i -e 's/{{KEYSTORE_PASSWORD}}/'"$KEYSTORE_PASSWORD"'/g' $defaultConf
 sed -i -e 's/{{KEY_PASSWORD}}/'"$KEY_PASSWORD"'/g'           $defaultConf
 
 echo 
 
 if [ "$1" = "http" -o "$1" = "" ] ; then 
   echo " keycloak will start at : http://localhost:8180 "
 fi

 if [ "$1" = "https" ] ; then 
   echo " keycloak will start at : https://localhost:8543 "
 fi
 
 echo
 
 sleep 2
 
 $KEYCLOAK_DIRECTORY_INSTALLATION/bin/standalone.sh  -b 0.0.0.0 -Djboss.socket.binding.port-offset=100 &

 sleep 3
 
 ## FROM DOCKER VARIABLE_ENV
 if [[ "$MODE" = "DEMO" ]]  ; then  
   
     ./keycloak_client.sh
 fi
 
