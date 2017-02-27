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
