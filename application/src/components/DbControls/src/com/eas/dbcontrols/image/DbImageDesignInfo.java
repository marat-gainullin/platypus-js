/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.image;

import com.eas.controls.DesignInfo;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;

/**
 *
 * @author mg
 */
public class DbImageDesignInfo extends DbControlDesignInfo {

    protected boolean plain;

    public DbImageDesignInfo() {
        super();
    }

    public boolean isPlain() {
        return plain;
    }

    public void setPlain(boolean aValue) {
        boolean oldValue = plain;
        plain = aValue;
        firePropertyChange("plaint", oldValue, plain);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final DbImageDesignInfo other = (DbImageDesignInfo) obj;
        if (plain != other.isPlain()) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        super.assign(aSource);
        if (aSource instanceof DbImageDesignInfo) {
            DbImageDesignInfo aInfo = (DbImageDesignInfo) aSource;
            setPlain(aInfo.isPlain());
        }
    }
    
    public Class<?> getControlClass() {
        return DbImage.class;
    }

    @Override
    protected void accept(DbControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
