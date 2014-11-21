/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.visitors;

import com.eas.controls.visitors.ControlClassFinder;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.dbcontrols.check.DbCheck;
import com.eas.dbcontrols.check.DbCheckDesignInfo;
import com.eas.dbcontrols.combo.DbCombo;
import com.eas.dbcontrols.combo.DbComboDesignInfo;
import com.eas.dbcontrols.date.DbDate;
import com.eas.dbcontrols.date.DbDateDesignInfo;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.image.DbImage;
import com.eas.dbcontrols.image.DbImageDesignInfo;
import com.eas.dbcontrols.label.DbLabel;
import com.eas.dbcontrols.label.DbLabelDesignInfo;
import com.eas.dbcontrols.spin.DbSpin;
import com.eas.dbcontrols.spin.DbSpinDesignInfo;
import com.eas.dbcontrols.text.DbText;
import com.eas.dbcontrols.text.DbTextDesignInfo;

/**
 *
 * @author mg
 */
public class DbControlClassFinder extends ControlClassFinder implements DbControlsDesignInfoVisitor {

    @Override
    public void visit(DbCheckDesignInfo aInfo) {
        result = DbCheck.class;
    }

    @Override
    public void visit(DbComboDesignInfo aInfo) {
        result = DbCombo.class;
    }

    @Override
    public void visit(DbDateDesignInfo aInfo) {
        result = DbDate.class;
    }

    @Override
    public void visit(DbImageDesignInfo aInfo) {
        result = DbImage.class;
    }

    @Override
    public void visit(DbLabelDesignInfo aInfo) {
        result = DbLabel.class;
    }

    @Override
    public void visit(DbSpinDesignInfo aInfo) {
        result = DbSpin.class;
    }

    @Override
    public void visit(DbTextDesignInfo aInfo) {
        result = DbText.class;
    }

    @Override
    public void visit(DbGridDesignInfo aInfo) {
        result = DbGrid.class;
    }
}
