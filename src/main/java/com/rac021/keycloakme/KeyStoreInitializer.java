
package com.rac021.keycloakme ;

import java.io.File ;
import java.security.KeyStore ;
import java.io.FileOutputStream ;

/**
 *
 * @author ryahiaoui
 */
public class KeyStoreInitializer {
    
    
    public static KeyStore initilize( String key ) {
        
        KeyStore keystore  = null ;
        
        /* Initialise KeyStore for SSL Connections */
        
        try {
            
             String filename = "cacerts"                    ;
                
             File f = new File(filename)                    ;
             if( ! f.exists() ) f.createNewFile()           ;
             
             keystore = KeyStore.getInstance(KeyStore.getDefaultType()) ;

             char[] password = key.toCharArray() ;
             keystore.load( null, password )     ;

             // Store away the keystore.
             try ( FileOutputStream fos = new FileOutputStream(filename) ) {
                keystore.store(fos, password )   ;
             }

        } catch( Exception ex )      {
            System.out.println( ex ) ;
        }
        
        return keystore ;
    }
    
}
