/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.text;

import com.eas.controls.DesignInfo;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.dbcontrols.label.DbLabelDesignInfo;

/**
 *
 * @author mg
 */
public class DbTextDesignInfo extends DbLabelDesignInfo {

    public DbTextDesignInfo() {
        super();
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final DbTextDesignInfo other = (DbTextDesignInfo) obj;
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        super.assign(aSource);
        if (aSource instanceof DbTextDesignInfo) {
            DbTextDesignInfo aInfo = (DbTextDesignInfo) aSource;
        }
    }

    @Override
    public Class<?> getControlClass() {
        return DbText.class;
    }

    @Override
    protected void accept(DbControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
