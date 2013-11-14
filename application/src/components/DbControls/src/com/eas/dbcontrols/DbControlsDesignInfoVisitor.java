/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.dbcontrols.check.DbCheckDesignInfo;
import com.eas.dbcontrols.combo.DbComboDesignInfo;
import com.eas.dbcontrols.date.DbDateDesignInfo;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.image.DbImageDesignInfo;
import com.eas.dbcontrols.label.DbLabelDesignInfo;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.scheme.DbSchemeDesignInfo;
import com.eas.dbcontrols.spin.DbSpinDesignInfo;
import com.eas.dbcontrols.text.DbTextDesignInfo;

/**
 *
 * @author mg
 */
public interface DbControlsDesignInfoVisitor extends ControlsDesignInfoVisitor {

    // db controls
    public void visit(DbCheckDesignInfo aInfo);

    public void visit(DbComboDesignInfo aInfo);

    public void visit(DbDateDesignInfo aInfo);

    public void visit(DbImageDesignInfo aInfo);

    public void visit(DbLabelDesignInfo aInfo);

    public void visit(DbSchemeDesignInfo aInfo);

    public void visit(DbSpinDesignInfo aInfo);

    public void visit(DbTextDesignInfo aInfo);

    public void visit(DbGridDesignInfo aInfo);

    public void visit(DbMapDesignInfo aInfo);
}
