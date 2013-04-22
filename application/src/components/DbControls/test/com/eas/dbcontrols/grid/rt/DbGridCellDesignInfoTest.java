/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.rt;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.model.ModelElementRef;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.grid.DbGridCellDesignInfo;
import com.eas.dbcontrols.label.DbLabelDesignInfo;
import static org.junit.Assert.*;
import org.junit.Test;
/**
 *
 * @author mg
 */
public class DbGridCellDesignInfoTest {

    @Test
    public void assignEqualsTest()
    {
        DbGridCellDesignInfo ethalon = new DbGridCellDesignInfo();
        DbGridCellDesignInfo sample1 = new DbGridCellDesignInfo();
        DbGridCellDesignInfo sample2 = new DbGridCellDesignInfo();
        assertTrue(ethalon.isEqual(sample1));
        assertTrue(ethalon.isEqual(sample2));

        DbControlDesignInfo controlInfo = new DbLabelDesignInfo();
        controlInfo.setSelectFunction("labelSelectFunction");
        ModelElementRef rowsKeyFieldRef = new ModelElementRef();
        rowsKeyFieldRef.setEntityId(IDGenerator.genID());
        ModelElementRef cellValueRef = new ModelElementRef();
        cellValueRef.setEntityId(IDGenerator.genID());
        ModelElementRef columnsKeyFieldRef = new ModelElementRef();
        columnsKeyFieldRef.setEntityId(IDGenerator.genID());

        sample1.setCellControlInfo(controlInfo);
        sample1.setCellValueField(cellValueRef);
        sample1.setColumnsKeyField(columnsKeyFieldRef);
        sample1.setRowsKeyField(rowsKeyFieldRef);

        sample2.assign(sample1);

        assertTrue(controlInfo.isEqual(sample2.getCellControlInfo()));
        assertEquals(cellValueRef, sample2.getCellValueField());
        assertEquals(columnsKeyFieldRef, sample2.getColumnsKeyField());
        assertEquals(rowsKeyFieldRef, sample2.getRowsKeyField());
        
        assertTrue(sample1.isEqual(sample2));

        assertFalse(ethalon.isEqual(sample1));
        assertFalse(ethalon.isEqual(sample2));
        sample2.assign(ethalon);
        
        assertTrue(ethalon.isEqual(sample2));
    }
}
