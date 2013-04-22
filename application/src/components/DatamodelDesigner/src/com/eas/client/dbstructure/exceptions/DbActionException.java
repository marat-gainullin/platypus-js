/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.dbstructure.exceptions;

import com.eas.client.dbstructure.DbStructureUtils;

/**
 *
 * @author mg
 */
public class DbActionException extends Exception{

    protected String param1 = null;
    protected String param2 = null;
    
    public DbActionException(String aCourse)
    {
        super(aCourse);
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    @Override
    public String getLocalizedMessage() {
        if(param1 != null)
            return DbStructureUtils.getString(super.getMessage(), param1, param2);
        else
            return DbStructureUtils.getString(super.getMessage());
    }
}
