/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.compacts;

/**
 * CompactSqlXml objects hold compact, not streamed, not structured string data for SqlXml data type.
 * @author mg
 */
public class CompactSqlXml {

    protected String data;

    public CompactSqlXml(String aData)
    {
        super();
        setData(aData);
    }

    public void setData(String aData) {
        data = aData;
    }

    public String getData() {
        return data;
    }
}
