/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.dbcontrols.check.DbCheck;
import com.eas.dbcontrols.combo.DbCombo;
import com.eas.dbcontrols.date.DbDate;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.image.DbImage;
import com.eas.dbcontrols.label.DbLabel;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.scheme.DbScheme;
import com.eas.dbcontrols.spin.DbSpin;
import com.eas.dbcontrols.text.DbText;

/**
 *
 * @author mg
 */
public class ModelComponentWrapper {

    public static ModelCheckBox wrap(DbCheck aDelegate) {
        return new ModelCheckBox(aDelegate);
    }

    public static ModelCombo wrap(DbCombo aDelegate) {
        return new ModelCombo(aDelegate);
    }

    public static ModelDate wrap(DbDate aDelegate) {
        return new ModelDate(aDelegate);
    }

    public static ModelGrid wrap(DbGrid aDelegate) {
        return new ModelGrid(aDelegate);
    }

    public static ModelImage wrap(DbImage aDelegate) {
        return new ModelImage(aDelegate);
    }

    public static ModelMap wrap(DbMap aDelegate) {
        return new ModelMap(aDelegate);
    }

    public static ModelScheme wrap(DbScheme aDelegate) {
        return new ModelScheme(aDelegate);
    }

    public static ModelSpin wrap(DbSpin aDelegate) {
        return new ModelSpin(aDelegate);
    }

    public static ModelFormattedField wrap(DbLabel aDelegate) {
        return new ModelFormattedField(aDelegate);
    }

    public static ModelTextArea wrap(DbText aDelegate) {
        return new ModelTextArea(aDelegate);
    }
}
