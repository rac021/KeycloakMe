
package com.rac021.keycloakme ;

import java.net.URI ;
import java.util.Map ;
import java.util.List ;
import java.util.UUID ;
import java.util.Arrays ;
import java.util.HashMap;
import java.util.Optional ;
import java.util.LinkedList ;
import javax.ws.rs.core.Response ;
import org.keycloak.admin.client.Keycloak ;
import javax.ws.rs.WebApplicationException ;
import org.keycloak.admin.client.resource.RealmResource ;
import org.keycloak.protocol.oidc.mappers.HardcodedClaim;
import org.keycloak.representations.idm.RoleRepresentation ;
import org.keycloak.representations.idm.UserRepresentation ;
import org.keycloak.representations.idm.RealmRepresentation ;
import org.keycloak.representations.idm.ClientRepresentation ;
import org.keycloak.representations.idm.CredentialRepresentation ;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper ;
import org.keycloak.representations.idm.ProtocolMapperRepresentation ;

/**
 *
 * @author ryahiaoui
 */

public class Helper {
    
    
    public static UserRepresentation InstanciateUser( String username, String password ) {
        
        UserRepresentation user = new UserRepresentation() ;
        user.setUsername(username)                         ;
        user.setId(UUID.randomUUID().toString())           ;
        user.setEnabled(true)                              ;
        
        List<CredentialRepresentation> credentials = createCredentials(password) ;
        user.setCredentials(credentials)                                         ;
        
        return user ;
    }
    
    public static RealmRepresentation removeAndCreateRealm( Keycloak kc        , 
                                                            String   realmName ,
                                                            boolean  recreate  ) {
      
        Optional<RealmRepresentation> findFirst =
                
                kc.realms().findAll()
                           .stream().filter( r -> r.getRealm().equals(realmName))
                           .findFirst()    ;
        
        if ( ! recreate && findFirst.isPresent() )                {
            // Already Exists 
             System.out.println( " Realm ( " + realmName          +
                                 " ) FOUND AND WILL BE USED " )   ;
            return findFirst.get()                                ;
        }
          
        if( recreate && findFirst.isPresent() )                   {
            
            // if recreate and exists, then remove ...
            try {
                 kc.realms().realm( realmName  ).remove()         ;
             } catch( Exception ex ) {
                 System.out.println( " Exeption : "               +
                                     ex.getMessage() )            ;
             }
        }
        
        RealmRepresentation realm = new RealmRepresentation()     ;
        realm.setRealm( realmName  )                              ;
        kc.realms().create(realm)                                 ;
        return realm                                              ;
     
    }
    
    public static List<CredentialRepresentation> createCredentials( String password ) {
        
       List<CredentialRepresentation> credentials = new LinkedList<>()      ;
       CredentialRepresentation credential = new CredentialRepresentation() ;
       credential.setType(CredentialRepresentation.PASSWORD)                ;
       credential.setValue(password)                                        ;
       credential.setTemporary(false)                                       ;
       credentials.add(credential)                                          ;
       return credentials                                                   ;
    }
    
    public static ClientRepresentation instanciateClient( String clientID  ,
                                                          String secretID  ,
                                                          String protocole )   {
        
        ClientRepresentation clientRepresentation = new ClientRepresentation() ;
        clientRepresentation.setClientId(clientID)                             ;
        clientRepresentation.setName(clientID)                                 ;
        clientRepresentation.setProtocol(protocole)                            ;
        clientRepresentation.setPublicClient(false)                            ;
        clientRepresentation.setStandardFlowEnabled(false)                     ;
        clientRepresentation.setBearerOnly(false)                              ;
        clientRepresentation.setDirectAccessGrantsEnabled(true)                ;
        clientRepresentation.setSecret(secretID)                               ;
        clientRepresentation.setId(UUID.randomUUID().toString())               ;
        
        return clientRepresentation                                            ;
        
    }
        
    public static void removeClient( Keycloak kc         ,
                                     String   realmName  ,
                                     String   clientUuId ) {
        
        kc.realms().realm( realmName ).clients().get(clientUuId).remove() ;
    }
        
    public static void removeUser ( Keycloak kc         ,
                                    String   realmName  ,
                                    String   userUuId ) {
       
        kc.realms().realm( realmName ).users().get(userUuId).remove() ;
        
        
    }
        
    public static Response createAndSaveUserInRealm( Keycloak kc     ,
                                                     String realm    ,
                                                     String userName ,
                                                     String password ) {
        
      UserRepresentation user = InstanciateUser( userName, password ) ;
      
      boolean userAlreadyExists = ! kc.realm(realm).users().search(userName).isEmpty() ;
      
      if( userAlreadyExists ) return null ;
      
      return kc.realm(realm).users().create(user)  ;
        
    }
    
    public static String checkIfExistsUserByUserName( Keycloak kc       ,
                                                      String   realm    ,
                                                      String   userName ) {
      
        List<UserRepresentation> user = kc.realm(realm).users().search(userName);
        if( user.isEmpty() ) return null ;
      
      return user.get(0).getId()  ;
        
    }
    
    public static RoleRepresentation instanciateRole( String roleName ) {
        
         // Create the role
        RoleRepresentation role   = new RoleRepresentation() ;
        role.setName(roleName)                               ;
        role.setClientRole(true)                             ;
        role.setId(roleName)                                 ;                                     
        return role                                          ;
        
    }
    
    public static void assignRoleToClient( Keycloak kc     ,
                                           String realm    ,
                                           String clientId , 
                                           RoleRepresentation role ) {
        
        Optional<RoleRepresentation> ex = kc.realm(realm  ).clients().get(clientId).roles().list()
                .stream().filter( rr -> rr.getName().equals(role.getName())).findFirst();
        
        if( ! ex.isPresent() ) {
          kc.realm(realm  ).clients().get(clientId).roles().create(role) ;
        } else {
            System.out.println( " Role ( " + role.getName()             +
                                " ) Already set to the Client UUID ( "  +
                                clientId + " ) in the Realm ( " + realm + " )" ) ;
        }
    }
    
    public static Response saveClientInRealm( Keycloak kc  , 
                                              String realm , 
                                              ClientRepresentation client )  {

        RealmResource resourceRealm = kc.realms().realm( realm  )     ;

        return resourceRealm.clients().create(client ) ;
    }
    
    public static Optional<ClientRepresentation> alreadyExistsClientInRealm ( Keycloak kc  , 
                                                                              String realm , 
                                                                              String client_id )  {
        return  kc.realms().realm( realm  )
                           .clients()
                           .findAll()
                           .stream().filter( r -> r.getClientId().equals(client_id))
                           .findFirst() ;
    }
    
    public static RoleRepresentation getAvailableClientRolesForUser( Keycloak kc       ,
                                                                     String   realm    , 
                                                                     String   clientId , 
                                                                     String   userId   ,
                                                                     String   roleName ) {
        Optional<RoleRepresentation> role = 
                                            
                                        kc.realm(realm  )
                                          .users()
                                          .get(userId)
                                          .roles()
                                          .clientLevel(clientId)
                                          .listEffective()
                                          .stream()
                                          .filter( roleRep -> roleRep.getName().equals(roleName))
                                          .findFirst() ;
        
        if( role.isPresent() )  return role.get() ;
        
                                      role = 
                
                                        kc.realm(realm  )
                                          .users()
                                          .get(userId)
                                          .roles()
                                          .clientLevel(clientId)
                                          .listAvailable()
                                          .stream()
                                          .filter( roleRep -> roleRep.getName().equals(roleName))
                                          .findFirst() ;
        
        return  role.isPresent() ? role.get() : null   ;

    }
    
    public static void assignClientRoleToUser( Keycloak kc     , 
                                               String realm    , 
                                               String clientId , 
                                               String userId   , 
                                               RoleRepresentation role )  {
       
      kc.realm( realm  ).users().get(userId).roles()
                        .clientLevel(clientId).add(Arrays.asList(role))   ;
    }
    
    public static String getUUIDFromResponse(Response response)  {
        
        URI location = response.getLocation()                    ;
        
        if (response.getStatusInfo().equals(Response.Status.CONFLICT))    {
            
            System.out.println( " ++ Conflict --> " + 
                                response.readEntity(String.class)) ;
        }
        
        if ( ! response.getStatusInfo().equals(Response.Status.CREATED))  {
            
            Response.StatusType statusInfo = response.getStatusInfo()     ;
            
            throw new WebApplicationException ( "Create method returned status "
                                                + statusInfo.getReasonPhrase()                 +
                                                " (Code: " + statusInfo.getStatusCode()        + 
                                                "); expected status: Created (201)", response) ;
        }
        
        if ( location == null ) return null                ;
        
        String path = location.getPath()                   ;
        return path.substring( path.lastIndexOf('/') + 1 ) ;
    }
    
    public static ProtocolMapperRepresentation createProtocolMapperModel( String  name           ,
                                                                          String  protocol       ,
                                                                          String  protocolMapper , 
                                                                          String  tokenClaimName ,
                                                                          String  claimValue     , 
                                                                          String  claimType      , 
                                                                          boolean accessToken    ) {
          
        ProtocolMapperRepresentation fooMapper = new ProtocolMapperRepresentation()                ;
        fooMapper.setName(name)                                                                    ;
        fooMapper.setProtocol(protocol)                                                            ;
        fooMapper.setProtocolMapper(protocolMapper)                                                ;
        
        Map<String, String> config = new HashMap<>()                                               ;
        config.put(HardcodedClaim.CLAIM_VALUE, claimValue)                                         ;
        config.put(OIDCAttributeMapperHelper.JSON_TYPE, claimType)                                 ;
        config.put(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME, tokenClaimName)                     ;
        config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN, String.valueOf(accessToken)) ;
        
        fooMapper.setConfig(config)                                                                ;
        return fooMapper                                                                           ;
    }

}
