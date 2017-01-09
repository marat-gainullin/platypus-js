/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */

package com.eas.client.queries;

/**
 *
 * @author Marat
 */
public class ParameterBindSpec extends Object{
    protected String datasourceName = null;// null means that parameter is binded to query parameter
    protected String datasourecField = null; 

    public ParameterBindSpec()
    {
        super();
    }
    
    public ParameterBindSpec(String aDatasourceName, String aDatasourecField)
    {
        super();
        datasourceName = aDatasourceName;
        datasourecField = aDatasourecField;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public void setDatasourecField(String datasourecField) {
        this.datasourecField = datasourecField;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public String getDatasourecField() {
        return datasourecField;
    }
}
