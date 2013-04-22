/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.scheme;

import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;

/**
 *
 * @author mg
 */
public class DbSchemeDesignInfo extends DbControlDesignInfo {

    public DbSchemeDesignInfo() {
        super();
    }

    public Class<?> getControlClass() {
        return DbScheme.class;
    }

    @Override
    protected void accept(DbControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
