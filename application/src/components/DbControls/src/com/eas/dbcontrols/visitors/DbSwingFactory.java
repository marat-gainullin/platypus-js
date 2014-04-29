/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.visitors;

import com.eas.client.model.application.ApplicationModel;
import com.eas.controls.ControlDesignInfo;
import com.eas.controls.ControlsUtils;
import com.eas.controls.visitors.SwingFactory;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.dbcontrols.ScalarDbControl;
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
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.scheme.DbScheme;
import com.eas.dbcontrols.scheme.DbSchemeDesignInfo;
import com.eas.dbcontrols.spin.DbSpin;
import com.eas.dbcontrols.spin.DbSpinDesignInfo;
import com.eas.dbcontrols.text.DbText;
import com.eas.dbcontrols.text.DbTextDesignInfo;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

/**
 *
 * @author mg
 */
public class DbSwingFactory extends SwingFactory implements DbControlsDesignInfoVisitor {

    protected ApplicationModel<?, ?, ?, ?> model;

    public DbSwingFactory(ApplicationModel<?, ?, ?, ?> aModel) {
        super();
        model = aModel;
    }

    public DbSwingFactory() {
        super();
    }

    @Override
    protected Class<?> findClassByDesignInfo(ControlDesignInfo aDi) {
        DbControlClassFinder clsFinder = new DbControlClassFinder();
        aDi.accept(clsFinder);
        return clsFinder.getResult();
    }

    @Override
    public void processControlProperties(Component comp, ControlDesignInfo aInfo) {
        super.processControlProperties(comp, aInfo);
        if (comp instanceof DbControlPanel && aInfo instanceof DbControlDesignInfo) {
            DbControlPanel dbc = (DbControlPanel) comp;
            DbControlDesignInfo dbci = (DbControlDesignInfo) aInfo;
            dbc.setEditable(dbci.isEditable());
        }
    }

    protected void visitModelScalarControl(final DbControlDesignInfo aInfo) throws Exception {
        assert comp instanceof ScalarDbControl;
        final ScalarDbControl control = (ScalarDbControl) comp;
        control.setModel(model);
        control.setDatamodelElement(aInfo.getDatamodelElement());
    }

    @Override
    public void visit(DbCheckDesignInfo aInfo) {
        try {
            visitControl(aInfo);
            assert comp instanceof DbCheck;
            DbCheck ch = (DbCheck) comp;
            checkCheckLFProps(aInfo);
            processControlEvents(aInfo);
            visitModelScalarControl(aInfo);
            ch.setText(aInfo.getText());
        } catch (Exception ex) {
            Logger.getLogger(DbSwingFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void visit(DbComboDesignInfo aInfo) {
        try {
            visitControl(aInfo);
            assert comp instanceof DbCombo;
            DbCombo modelCombo = (DbCombo) comp;
            checkTextLFProps(aInfo);
            processControlEvents(aInfo);
            visitModelScalarControl(aInfo);
            modelCombo.setList(aInfo.isList());
            modelCombo.setValueField(aInfo.getValueField());
            modelCombo.setDisplayField(aInfo.getDisplayField());
            modelCombo.setEmptyText(aInfo.getEmptyText());
        } catch (Exception ex) {
            Logger.getLogger(DbSwingFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void visit(DbDateDesignInfo aInfo) {
        try {
            visitControl(aInfo);
            assert comp instanceof DbDate;
            DbDate modelDate = (DbDate) comp;
            checkTextLFProps(aInfo);
            processControlEvents(aInfo);
            visitModelScalarControl(aInfo);
            modelDate.setDateFormat(aInfo.getDateFormat());
            modelDate.setExpanded(aInfo.isExpanded());
            modelDate.setEmptyText(aInfo.getEmptyText());
        } catch (Exception ex) {
            Logger.getLogger(DbSwingFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void visit(DbImageDesignInfo aInfo) {
        try {
            visitControl(aInfo);
            assert comp instanceof DbImage;
            DbImage modelImage = (DbImage) comp;
            processControlEvents(aInfo);
            visitModelScalarControl(aInfo);
            modelImage.setPlain(aInfo.isPlain());
        } catch (Exception ex) {
            Logger.getLogger(DbSwingFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void visit(DbLabelDesignInfo aInfo) {
        try {
            visitControl(aInfo);
            assert comp instanceof DbLabel;
            DbLabel modelLabel = (DbLabel) comp;
            checkTextLFProps(aInfo);
            processControlEvents(aInfo);
            visitModelScalarControl(aInfo);
            modelLabel.setFormatterFactory(ControlsUtils.formatterFactoryByFormat(aInfo.getFormat(), aInfo.getValueType()));
            modelLabel.setEmptyText(aInfo.getEmptyText());
        } catch (Exception ex) {
            Logger.getLogger(DbSwingFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void visit(DbSchemeDesignInfo aInfo) {
        try {
            visitControl(aInfo);
            assert comp instanceof DbScheme;
            processControlEvents(aInfo);
            visitModelScalarControl(aInfo);
        } catch (Exception ex) {
            Logger.getLogger(DbSwingFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void visit(DbSpinDesignInfo aInfo) {
        try {
            visitControl(aInfo);
            assert comp instanceof DbSpin;
            DbSpin modelSpin = (DbSpin) comp;
            checkTextLFProps(aInfo);
            processControlEvents(aInfo);
            visitModelScalarControl(aInfo);
            modelSpin.setMin(aInfo.getMin());
            modelSpin.setMax(aInfo.getMax());
            modelSpin.setStep(aInfo.getStep());
            modelSpin.setEmptyText(aInfo.getEmptyText());
        } catch (Exception ex) {
            Logger.getLogger(DbSwingFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void visit(DbTextDesignInfo aInfo) {
        try {
            visitControl(aInfo);
            assert comp instanceof DbText;
            DbText modelText = (DbText) comp;
            checkTextLFProps(aInfo);
            modelText.setEmptyText(aInfo.getEmptyText());
            processControlEvents(aInfo);
            visitModelScalarControl(aInfo);
        } catch (Exception ex) {
            Logger.getLogger(DbSwingFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void checkCheckLFProps(ControlDesignInfo aInfo) {
        JCheckBox sampleTxt = new JCheckBox();
        if (aInfo.getBackground() == null) {
            comp.setBackground(sampleTxt.getBackground());
        }
        if (aInfo.getForeground() == null) {
            comp.setForeground(sampleTxt.getForeground());
        }
        if (aInfo.getFont() == null) {
            comp.setFont(sampleTxt.getFont());
        }
    }

    private void checkTextLFProps(ControlDesignInfo aInfo) {
        JTextField sampleTxt = new JTextField();
        if (aInfo.getBackground() == null) {
            comp.setBackground(sampleTxt.getBackground());
        }
        if (aInfo.getForeground() == null) {
            comp.setForeground(sampleTxt.getForeground());
        }
        if (aInfo.getFont() == null) {
            comp.setFont(sampleTxt.getFont());
        }
    }

    @Override
    public void visit(final DbGridDesignInfo aInfo) {
        try {
            visitControl(aInfo);
            assert comp instanceof DbGrid;
            final DbGrid grid = (DbGrid) comp;
            grid.setModel(model);
            processControlEvents(aInfo);
            grid.setRowsDatasource(aInfo.getRowsDatasource());
            grid.setUnaryLinkField(aInfo.getUnaryLinkField());
            grid.setParam2GetChildren(aInfo.getParam2GetChildren());
            grid.setParamSourceField(aInfo.getParamSourceField());
            for (int i = 0; i < aInfo.getHeader().size(); i++) {
                grid.getHeader().add(aInfo.getHeader().get(i).copy());
            }
            grid.setRowsHeaderType(aInfo.getRowsHeaderType());
            grid.setFixedColumns(aInfo.getFixedColumns());
            grid.setFixedRows(aInfo.getFixedRows());
            grid.setInsertable(aInfo.isInsertable());
            grid.setDeletable(aInfo.isDeletable());
            grid.setEditable(aInfo.isEditable());
            grid.setShowHorizontalLines(aInfo.isShowHorizontalLines());
            grid.setShowVerticalLines(aInfo.isShowVerticalLines());
            grid.setShowOddRowsInOtherColor(aInfo.isShowOddRowsInOtherColor());
            grid.setRowsHeight(aInfo.getRowsHeight());
            grid.setOddRowsColor(aInfo.getOddRowsColor());
            grid.setGridColor(aInfo.getGridColor());
        } catch (Exception ex) {
            Logger.getLogger(DbSwingFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void visit(final DbMapDesignInfo aInfo) {
        try {
            visitControl(aInfo);
            assert comp instanceof DbMap;
            final DbMap map = (DbMap) comp;
            processControlEvents(aInfo);
            map.setModel(model);
            map.setMapTitle(aInfo.getMapTitle());
            map.setBackingUrl(aInfo.getBackingUrl());
            map.setCrsWkt(aInfo.getCrsWkt());
            map.getFeatures().addAll(aInfo.getFeatures());
        } catch (Exception ex) {
            Logger.getLogger(DbSwingFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
