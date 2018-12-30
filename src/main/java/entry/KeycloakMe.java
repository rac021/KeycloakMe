
package entry ;

import java.util.Map ;
import java.util.UUID ;
import java.util.List ;
import java.util.Arrays ;
import java.util.HashMap ;
import java.util.Optional ;
import java.nio.file.Path ;
import java.nio.file.Paths ; 
import java.io.IOException ;
import java.nio.file.Files ;
import java.security.KeyStore ;
import javax.ws.rs.core.Response ;
import com.rac021.keycloakme.IOManager ;
import org.keycloak.admin.client.Keycloak ;
import static com.rac021.keycloakme.Helper.* ;
import com.rac021.keycloakme.KeyStoreInitializer;
import org.keycloak.admin.client.KeycloakBuilder ;
import org.keycloak.representations.idm.RoleRepresentation ;
import org.keycloak.representations.idm.ClientRepresentation ;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder ;
import org.keycloak.representations.idm.ProtocolMapperRepresentation ;

/**
 *
 * @author ryahiaoui
 */

public class KeycloakMe {
        
    static Map<String, String> clientsIDs  = new HashMap<>()   ;
    
    final static String RECREATE = "REMOVE-IF-EXITS"           ;
   
    static KeyStore keystore                                   ; 

    
    public static void main (String[] args) throws IOException {
          
         String path = "keycloak.txt"  ;
         
         if( args.length >= 1 )        {
             path = args[0]            ;
         }

         System.out.println ( "   " )            ;
         System.out.println(" Path : " + path  ) ;
         System.out.println ( "   " )            ;
         
         Path pathFile = Paths.get( path )       ;
        
         if( ! Files.exists(pathFile ) )                                         {
             
             System.out.println(" ")                                             ;
             System.out.println(" No file Provided. Please, restart passing in " +
                                "the first argument the path of the file."       +
                                "\n You can also put in the same Directory as "  +
                                "the jar a file with the name keycloak.txt " )   ;
             System.out.println(" ")                                             ;
             System.exit( 0 )                                                    ;
             
         }
  
        Optional<String> optionalUrl = IOManager.getURL(path)             ;
        
        if( !optionalUrl.isPresent() )  {
            throw new RuntimeException(" No URL was provided in the file ") ;
        }
        
        Optional<String> optionalMasterRealm = IOManager.getMasterREALM(path)      ;
        
        if( !optionalMasterRealm.isPresent() ) {
            throw new RuntimeException(" No Master_Realm was provided in the file ") ;
        }
        
        String []masterRealm = optionalMasterRealm.get().split(",") ;
        
        String masterRealmName     = masterRealm[0].trim()    ;
        String masterRealmClientID = masterRealm[1].trim()    ;
        String masterRealmUserName = masterRealm[2].trim()    ;
        String masterRealmPassword = masterRealm[3].trim()    ;
        String appProtocole        = masterRealm[4].trim()    ;
        
        String SERVER_URL          = optionalUrl.get().trim() ;
        
        /* Authentication */
       
        String key = null ;
        
        if( SERVER_URL.toLowerCase().startsWith("https") )        {
            
           System.out.println( " MODE HTTPS ENABLED -> "          +
                               "AUTO Generate SSL Certificate " ) ;
           System.out.println( "                       " )        ;
          
           key  = UUID.randomUUID().toString()                    ;
           keystore = KeyStoreInitializer.initilize( key )        ;
        }
        
        Keycloak kc = KeycloakBuilder.builder()
                                     .serverUrl(SERVER_URL)
                                     .realm(masterRealmName)
                                     .username(masterRealmUserName)
                                     .password(masterRealmPassword)
                                     .clientId(masterRealmClientID)
                                     .resteasyClient (
                                        new ResteasyClientBuilder()
                                            .connectionPoolSize(2)
                                            .keyStore(keystore, key)
                                            .build() )
                                     .build() ;
       
        List<String> realms  = IOManager.buildListRealms(path)  ;
        List<String> clients = IOManager.buildListClients(path) ;
        List<String> users   = IOManager.buildListUsers(path)   ;
        
        
        manageRealms  ( kc , realms )                ;
        
        manageClients ( kc , clients, appProtocole ) ;
        
        manageUsers   ( kc , users  )                ;
       
    }
    
    
    private static void manageRealms( Keycloak kc , List<String> realms ) {
        
          realms.forEach( line ->                           {

            String  realm    = line.split(",")[0].trim()    ;
            boolean recreate = line.split(",").length >= 2 && 
                               line.split(",")[1].trim().equals(RECREATE)    ;
            if( recreate ) {
                 System.out.println( " Remove And Re Create The REALM ( "    + 
                                     realm + " )")                           ;
            }
            else {
                 System.out.println( " Create The REALM ( " +  realm + " )") ;
            }
            removeAndCreateRealm( kc, realm  , recreate ) ;
            
        } ) ;
    }

    private static void manageClients( Keycloak kc , List<String> clients, String appProtocole ) {
        
         clients.forEach( line -> {
            
             String realm      = line.split(",")[0].trim()                   ;
             String clientID   = line.split(",")[1].trim()                   ;
             String secretID   = line.split(",")[2].trim()                   ;
             String roles      = line.split(",")[3].trim()                   ;
             boolean recreate = line.split(",").length >= 5 && 
                                line.split(",")[4].trim().equals( RECREATE ) ;
            
            Optional<ClientRepresentation> existClient = alreadyExistsClientInRealm( kc         ,
                                                                                     realm      ,
                                                                                     clientID ) ;
           
            String clientUuid  =  null       ;
            
            if( recreate && existClient.isPresent() ) removeClient( kc    , 
                                                                    realm , 
                                                                    existClient.get().getId()  ) ;
             
            if ( ! existClient.isPresent() || recreate /* Because Client Already removed */ )    {
                 
                ClientRepresentation client = instanciateClient( clientID , 
                                                                 secretID , 
                                                                 appProtocole ) ;
            
                /** With recent keycloak version , the client id is apparently no longer
                    automatically added to the audience field 'aud' of the access token,
                    So, we have to create a mapper and overwriting the aud claim.     */
                    
                ProtocolMapperRepresentation createProtocolMapperModel  = 
                         
                                        createProtocolMapperModel( "audience"                    ,
                                                                   "openid-connect"              ,
                                                                   "oidc-hardcoded-claim-mapper" ,
                                                                   "aud"                         ,
                                                                   clientID                      ,
                                                                   "String"                      ,
                                                                   "true"                      ) ;
                 
                client.setProtocolMappers(Arrays.asList(createProtocolMapperModel ) )            ;
                    
                clientUuid = client.getId()                                                      ;
                
                try ( Response responseSavedClientInRealm = saveClientInRealm( kc      ,
                                                                               realm   , 
                                                                               client )) {
                    
                   clientUuid = getUUIDFromResponse( responseSavedClientInRealm )        ;
                   
                   System.out.println( " Created Client ( " + clientID                   +
                                       " )  In the Realm ( " + realm + " ) With - UUID : "
                                       + clientUuid  )  ;
                }
                
            } else {
                
                clientUuid = existClient.get().getId()                  ;
                
                System.out.println( " Client ( "        + clientID      +
                                    " ) Already Exists in the Realm ( " +
                                    realm  + " ) AND Will be Used "     + 
                                    " - UUID : " + clientUuid       )   ;
            }

            clientsIDs.put (  realm + "_" + clientID ,  clientUuid )   ;
            
            List<String> listRoles = Arrays.asList( roles.split(" ") ) ;
            
             String _clientUuid = clientUuid                           ;
             
            listRoles.forEach( roleString -> {

                System.out.println(" Create Role ( " + roleString + " )" ) ;
                RoleRepresentation role  = instanciateRole(roleString )    ;
                System.out.println( " Assing Role ( " + roleString  + " ) to the Client ( " +
                                    clientID + " ) in the Realm ( " + realm + " ) "   )     ;
               
                assignRoleToClient ( kc, realm , _clientUuid , role )                       ;
            }) ;

        } ) ;
        
    }

    private static void manageUsers ( Keycloak kc, List<String> users ) {
       
         users.forEach( (String line) ->                  {
             
            String realm    = line.split(",")[0].trim()  ;
            String clientID = line.split(",")[1].trim()  ;
            String userName = line.split(",")[2].trim()  ;
            String password = line.split(",")[3].trim()  ;
            String roles    = line.split(",")[4].trim()  ;
             
            boolean recreate = line.split(",").length >= 6 && 
                               line.split(",")[5].trim().equals( RECREATE ) ;
              
            String userUuid   = checkIfExistsUserByUserName( kc,realm , userName ) ;
            
            if( userUuid != null && recreate )      {
                
                System.out.println( " Remove The User (" + userName +
                                    " ) From the Realm ( " + realm  + " ) " ) ;
                removeUser ( kc, realm , userUuid)  ;
            }
            
            if( userUuid == null || recreate /* User Already Removed */ )     {
                
                 /** CREATE NEW USER **/
                 Response responseSavedUserAdmin  = createAndSaveUserInRealm( kc          ,
                                                                              realm       ,
                                                                              userName    ,
                                                                              password  ) ;

                 userUuid     =      getUUIDFromResponse(responseSavedUserAdmin )         ;

                 System.out.println( " Created User   ( "           + 
                                     userName     +
                                     " )   - UUID : " + userUuid )  ;
            }
           
            List<String> listRoles = Arrays.asList(  roles.split(" ") ) ;
            
            String _userUuid = userUuid       ;
            
            listRoles.forEach( (String roleString) ->  {

                System.out.println(" Create Role ( " + roleString.trim() + " ) " ) ;
                 
                RoleRepresentation role  = instanciateRole( roleString.trim() )    ;
               
                String clientid = clientsIDs.get( realm + "_" + clientID )         ;

                if( clientid == null ) {
                    System.err.println(" /// Client ID ( " + clientID + " ) not found in the REALM ( " + realm  + " )") ;
                    return ;
                }
                RoleRepresentation available  =
                        
                        getAvailableClientRolesForUser( kc                                       ,
                                                        realm                                    ,
                                                        clientsIDs.get( realm + "_" + clientID ) ,
                                                        _userUuid                                ,
                                                        roleString  )                            ;
                
                if( available == null ) {
                    System.out.println( " /// Trying to assing the Client Role ( "   + 
                                        roleString  + " ) to the User ( "      +  userName + " ) " +
                                        " In the Realm ( " + realm + " ) But this Role was not "   +
                                        "yet assigned to the Client ( " + clientID  + " ) "  )     ;
                }
                else {
                    System.out.println( " Assign Client Role ( " + available.getName()  + 
                                        " ) to the User ( "  + userName + " ) " )       ; 
                   assignClientRoleToUser ( kc                                          , 
                                            realm                                       , 
                                            clientsIDs.get(  realm  + "_" + clientID )  ,
                                            _userUuid                                   ,
                                            available    )                              ;
                }
            } ) ;
            
        } ) ;
    }
        
}

