/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.eas.client.metadata.JdbcField;
import java.util.Set;

/**
 * Resolver incapsulates functionality, involved in fields types resolving
 * from/to RDBMS friendly form.
 *
 * @author mg
 */
public interface TypesResolver {

    public String toApplicationType(int aJdbcType, String aRDBMSType);
    
    public Set<String> getSupportedTypes();
    
    public boolean isSized(String aTypeName);

    public boolean isScaled(String aTypeName);
    
    public void resolveSize(JdbcField aField);

}
