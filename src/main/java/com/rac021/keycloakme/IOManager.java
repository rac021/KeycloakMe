
package com.rac021.keycloakme ;

import java.util.List ;
import java.util.Optional ;
import java.io.IOException ;
import java.nio.file.Files ;
import java.nio.file.Paths ;
import java.util.stream.Collectors ;

/**
 *
 * @author ryahiaoui
 */

public class IOManager  {
    
    public static Optional<String>  getURL ( String path) throws IOException {
        
        return Files.lines(Paths.get(path))
                    .map(line -> cleanLine(line))
                    .filter( line -> ! line.startsWith("#"))
                    .filter( line -> line.startsWith("URL ") || line.startsWith("URL:") )
                    .map( line -> line.split(":", 2 )[1].trim())
                    .findFirst() ;
    }
    
    public static Optional<String> getMasterREALM( String path) throws IOException    {
        
        return Files.lines(Paths.get(path))
                    .map(line -> cleanLine(line))
                    .filter( line -> ! line.startsWith("#"))
                    .filter( line -> line.startsWith("REALM_MASTER_AUTHENTICATION ") || 
                                     line.startsWith("REALM_MASTER_AUTHENTICATION:")  )
                    .map( line -> line.split(":")[1].trim())
                    .findFirst();
    }
    
    public static List<String> buildListRealms( String path) throws IOException {
        
       return Files.lines(Paths.get(path))
                   .map(line -> cleanLine(line))
                   .filter( line -> ! line.startsWith("#"))
                   .filter( line -> line.startsWith("REALM ") || line.startsWith("REALM:"))
                   .map( line -> line.split(":")[1].trim())
                   .collect ( Collectors.toList() ) ;
    }
    

    
    public static List<String> buildListClients( String path ) throws IOException {
        
       return Files.lines(Paths.get(path))
                   .map(line -> cleanLine(line))
                   .filter( line -> ! line.startsWith("#"))
                   .filter( line -> line.startsWith("CLIENT ")  || line.startsWith("CLIENT:"))
                   .map( line -> line.split(":")[1].trim())
                   .collect ( Collectors.toList() ) ;
    }
    
    public static List<String> buildListUsers( String path ) throws IOException {
        
       return Files.lines(Paths.get(path))
                   .map(line -> cleanLine(line))
                   .filter( line -> ! line.startsWith("#"))
                   .filter( line -> line.startsWith("USER ") || line.startsWith("USER:"))
                   .map( line -> line.split(":")[1].trim())
                   .collect ( Collectors.toList() ) ;
    }

    private static String cleanLine( String line ) {
        return line.replaceAll(" +", " ").trim()   ;
    }
    
}
