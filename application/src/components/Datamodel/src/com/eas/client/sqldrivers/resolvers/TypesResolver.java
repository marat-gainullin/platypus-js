/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import java.util.Set;

/**
 * Resolver incapsulates functionality, involved in fields types resolving
 * from/to RDBMS friendly form.
 *
 * @author mg
 */
public interface TypesResolver {

    public String toApplicationType(String aRDBMSType);
    
    public Set<String> getSupportedTypes();
    
    public boolean isSized(String aTypeName);

    public boolean isScaled(String aTypeName);

}
