/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.ModelEntityRef;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsUtils;
import com.eas.dbcontrols.check.DbCheckDesignInfo;
import com.eas.dbcontrols.grid.DbGridCellDesignInfo;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.gui.CascadedStyle;
import java.awt.Color;
import java.awt.Font;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class DbGridColumnTest {

    @Test
    public void assignEqualsTest() {
        DbGridColumn ethalon = new DbGridColumn();
        DbGridColumn sample1 = new DbGridColumn();
        DbGridColumn sample2 = new DbGridColumn();
        assertTrue(ethalon.isEqual(sample1));
        assertTrue(ethalon.isEqual(sample2));

        ModelElementRef dmRef = new ModelElementRef();
        dmRef.setEntityId(IDGenerator.genID());

        ModelElementRef rowsKeyFieldRef = new ModelElementRef();
        rowsKeyFieldRef.setEntityId(IDGenerator.genID());
        DbGridCellDesignInfo cellDesign = new DbGridCellDesignInfo();
        cellDesign.setRowsKeyField(rowsKeyFieldRef);
        ModelEntityRef columnsDsRef = new ModelEntityRef();
        columnsDsRef.setEntityId(IDGenerator.genID());
        ModelElementRef columnsDisplayFieldRef = new ModelElementRef();
        columnsDisplayFieldRef.setEntityId(IDGenerator.genID());
        DbControlDesignInfo controlInfo = new DbCheckDesignInfo();
        CascadedStyle style1 = new CascadedStyle();
        style1.setBackground(Color.blue);
        ModelEntityRef cellsDsRef = new ModelEntityRef();
        cellsDsRef.setEntityId(IDGenerator.genID());

        // complex properties
        sample1.setCellsDatasource(cellsDsRef);
        sample1.setColumnsDatasource(columnsDsRef);
        sample1.setColumnsDisplayField(columnsDisplayFieldRef);
        sample1.setControlInfo(controlInfo);
        sample1.setDatamodelElement(dmRef);

        // plain properties
        sample1.setHeaderStyle(style1);
        sample1.setCellDesignInfo(cellDesign);
        sample1.setReadonly(true);
        sample1.setEnabled(false);
        style1.setFont(DbControlsUtils.toFont(Font.decode(Font.SANS_SERIF)));
        sample1.setName("column_1");
        sample1.setSelectOnly(true);
        sample1.setTitle("column_1_title");
        sample1.setVisible(false);
        sample1.setWidth(7895);

        sample2.assign(sample1);

        assertEquals(cellsDsRef, sample2.getCellsDatasource());
        assertEquals(columnsDsRef, sample2.getColumnsDatasource());
        assertEquals(columnsDisplayFieldRef, sample2.getColumnsDisplayField());
        assertTrue(controlInfo.isEqual(sample2.getControlInfo()));
        assertEquals(dmRef, sample2.getDatamodelElement());

        assertEquals(sample2.getBackground(), Color.blue);
        assertTrue(sample2.getCellDesignInfo().isEqual(cellDesign));
        assertEquals(sample2.isReadonly(), true);
        assertEquals(sample2.isEnabled(), false);
        assertTrue(sample2.getHeaderStyle().getFont().isEqual(DbControlsUtils.toFont(Font.decode(Font.SANS_SERIF))));
        assertEquals(sample2.getName(), "column_1");
        assertEquals(sample2.isSelectOnly(), true);
        assertTrue(sample2.getHeaderStyle().isEqual(style1));
        assertEquals(sample2.getTitle(), "column_1_title");
        assertEquals(sample2.isVisible(), false);
        assertEquals(sample2.getWidth(), 7895);

        assertTrue(sample1.isEqual(sample2));

        sample2.assign(ethalon);
        assertTrue(ethalon.isEqual(sample2));
    }
}
