#!/bin/bash

 # IMPORTANT 
 # if $JKS_NAME changes, you have tu update the the name
 # in the standalone.xml file [ keystore path="keystore.jks".. ]
 # in the ( ssl part )
 JKS_NAME="keystore.jks"
 
 PATH_P12=`pwd`/keystore.p12

 PATH_JKS=`pwd`/$JKS_NAME

 P12_STORE_PASSWORD="MY_PASSWORD_021"
 
 # NB : FOR KEYCLOAK, 
 # $JKS_DEST_STORE_PASSWORD and $JKS_DEST_KEY_PASS 
 # HAVE TO BE THE SAME 
 JKS_DEST_STORE_PASSWORD="MY_PASSWORD_007"
 JKS_DEST_KEY_PASS="MY_PASSWORD_007"
 
 # JKS_KEY_PASS="MY_OTHER_PASSWORD_069"
 
 VALIDITY_IN_DAYS="10000"
 ALIAS="client"


 ## Delete ALIAS + the file if Already Exists 

 if [  -f "$PATH_JKS" ]; then
   keytool -delete -alias $ALIAS -keystore $PATH_JKS -storepass $JKS_DEST_STORE_PASSWORD
   rm $PATH_JKS
 fi

 if [ -f "$PATH_P12" ]; then
     rm $PATH_P12
 fi

 
 echo ; echo " Generate PKCS12 File.. "
 
 ## Gen Key ( P12 )
 keytool -genkeypair  -storetype PKCS12                                          \
                      -alias     $ALIAS                                          \
                      -keyalg    RSA                                             \
                      -keysize   2048                                            \
                      -keystore  $PATH_P12                                       \
                      -storepass $P12_STORE_PASSWORD                             \
                      -keypass   $P12_STORE_PASSWORD                             \
                      -validity  $VALIDITY_IN_DAYS                               \
                      -ext san=dns:localhost,dns:localhost,                      \
                      -dname "CN=rac021, OU=ID, O=rac021, L=Paris, S=Paris, C=YR" 

  
 ## IMPORT P12 to JKS 
 echo ; echo " Import PKCS12 File to JKS.. " ; echo 
 
 keytool -importkeystore -srckeystore   $PATH_P12                 \
                         -srcstoretype  pkcs12                    \
                         -srcalias      $ALIAS                    \
                         -destkeystore  $PATH_JKS                 \
                         -deststoretype jks                       \
                         -destalias     $ALIAS                    \
                         -srcstorepass  $P12_STORE_PASSWORD       \
                         -destkeypass   $JKS_DEST_KEY_PASS        \
                         -deststorepass $JKS_DEST_STORE_PASSWORD  
                         # -keypass       $JKS_KEY_PASS 
                         
 echo 

 if [ -f "$PATH_P12" ]; then
 
     # Delete the PKCS12 File 
     rm $PATH_P12 ; echo 
 fi
 
