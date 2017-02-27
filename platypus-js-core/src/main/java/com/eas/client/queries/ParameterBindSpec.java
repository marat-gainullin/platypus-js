package com.eas.client.queries;

/**
 *
 * @author mg
 */
public class ParameterBindSpec {

    protected String datasourceName;// null means that parameter is binded to query parameter
    protected String datasourecField;

    public ParameterBindSpec() {
        super();
    }

    public ParameterBindSpec(String aDatasourceName, String aDatasourecField) {
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
