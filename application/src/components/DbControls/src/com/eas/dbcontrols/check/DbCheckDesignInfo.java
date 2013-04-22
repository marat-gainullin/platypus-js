/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.check;

import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class DbCheckDesignInfo extends DbControlDesignInfo {

    protected String text;

    public DbCheckDesignInfo() {
        super();
    }

    @Serial
    public String getText() {
        return text;
    }

    @Serial
    public void setText(String aValue) {
        String oldValue = text;
        text = aValue;
        firePropertyChange("text", oldValue, text);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final DbCheckDesignInfo other = (DbCheckDesignInfo) obj;
        if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
            return false;
        }
        return true;
    }

    @Override
    protected void assign(DbControlDesignInfo aSource) {
        super.assign(aSource);
        if (aSource instanceof DbCheckDesignInfo) {
            DbCheckDesignInfo check = (DbCheckDesignInfo) aSource;
            text = check.text != null ? new String(check.text.toCharArray()) : null;
        }
    }

    @Override
    protected void accept(DbControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
