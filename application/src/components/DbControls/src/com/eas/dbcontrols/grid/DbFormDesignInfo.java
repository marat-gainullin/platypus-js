/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid;

import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.controls.FormDesignInfo;
import com.eas.dbcontrols.check.DbCheckDesignInfo;
import com.eas.dbcontrols.combo.DbComboDesignInfo;
import com.eas.dbcontrols.date.DbDateDesignInfo;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.image.DbImageDesignInfo;
import com.eas.dbcontrols.label.DbLabelDesignInfo;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.scheme.DbSchemeDesignInfo;
import com.eas.dbcontrols.spin.DbSpinDesignInfo;
import com.eas.dbcontrols.text.DbTextDesignInfo;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class DbFormDesignInfo extends FormDesignInfo {

    @Override
    public Object createPropertyObjectInstance(String aSimpleClassName) {
        if (DbCheckDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new DbCheckDesignInfo();
        } else if (DbComboDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new DbComboDesignInfo();
        } else if (DbDateDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new DbDateDesignInfo();
        } else if (DbImageDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new DbImageDesignInfo();
        } else if (DbLabelDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new DbLabelDesignInfo();
        } else if (DbSchemeDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new DbSchemeDesignInfo();
        } else if (DbSpinDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new DbSpinDesignInfo();
        } else if (DbTextDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new DbTextDesignInfo();
        } else if (DbGridDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new DbGridDesignInfo();
        } else if (DbGridColumn.class.getSimpleName().equals(aSimpleClassName)) {
            return new DbGridColumn();
        } else if (DbMapDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new DbMapDesignInfo();
        } else if (RowsetFeatureDescriptor.class.getSimpleName().equals(aSimpleClassName)) {
            return new RowsetFeatureDescriptor();
        } else {
            Object res = super.createPropertyObjectInstance(aSimpleClassName);
            if (res == null) {
                Logger.getLogger(DbFormDesignInfo.class.getName()).severe(String.format("Neither plain or db-controls nor layout or constraints found with such name:: %s", aSimpleClassName));
            }
            return res;
        }
    }
}
