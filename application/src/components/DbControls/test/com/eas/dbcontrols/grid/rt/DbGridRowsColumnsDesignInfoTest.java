/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.rt;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.model.ModelEntityRef;
import com.eas.dbcontrols.grid.DbGridRowsColumnsDesignInfo;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class DbGridRowsColumnsDesignInfoTest {

    @Test
    public void assignEqualsTest()
    {
        DbGridRowsColumnsDesignInfo ethalon = new DbGridRowsColumnsDesignInfo();
        DbGridRowsColumnsDesignInfo sample1 = new DbGridRowsColumnsDesignInfo();
        DbGridRowsColumnsDesignInfo sample2 = new DbGridRowsColumnsDesignInfo();

        assertTrue(ethalon.isEqual(sample1));
        assertTrue(ethalon.isEqual(sample2));
        // changing section
        ModelEntityRef rowsDsRef = new ModelEntityRef();
        rowsDsRef.setEntityId(IDGenerator.genID());

        sample1.setFixedColumns(40);
        sample1.setFixedRows(20);
        sample1.setRowsHeaderType(DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_CHECKBOX);
        sample1.setRowsDatasource(rowsDsRef);
        //
        sample2.assign(sample1);
        // comparing section
        assertEquals(40, sample1.getFixedColumns());
        assertEquals(20, sample1.getFixedRows());
        assertEquals(DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_CHECKBOX, sample1.getRowsHeaderType());
        assertEquals(rowsDsRef, sample1.getRowsDatasource());
        //
        assertTrue(sample1.isEqual(sample2));

        assertFalse(ethalon.isEqual(sample1));
        assertFalse(ethalon.isEqual(sample2));
        sample2.assign(ethalon);

        assertTrue(ethalon.isEqual(sample2));
    }

}
