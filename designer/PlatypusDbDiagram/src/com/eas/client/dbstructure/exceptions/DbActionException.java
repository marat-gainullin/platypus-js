/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.exceptions;

import com.eas.client.dbstructure.DbStructureUtils;
import java.util.MissingResourceException;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class DbActionException extends Exception {

    protected String param1;
    protected String param2;

    public DbActionException(String aCourse) {
        super(aCourse);
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String aValue) {
        param1 = aValue;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String aValue) {
        param2 = aValue;
    }

    @Override
    public String getLocalizedMessage() {
        try {
            if (param1 != null) {
                return NbBundle.getMessage(DbStructureUtils.class, super.getMessage(), param1, param2);
            } else {
                return NbBundle.getMessage(DbStructureUtils.class, super.getMessage());
            }
        } catch (MissingResourceException ex) {
            return super.getMessage();
        }
    }
}
