/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.rt;

import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.grid.DbGridRowsColumnsDesignInfo;
import com.eas.dbcontrols.grid.DbGridTreeDesignInfo;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
/**
 *
 * @author mg
 */
public class DbGridDesignInfoTest {

    @Test
    public void assignEqualsTest()
    {
        DbGridDesignInfo ethalon = new DbGridDesignInfo();
        DbGridDesignInfo sample1 = new DbGridDesignInfo();
        DbGridDesignInfo sample2 = new DbGridDesignInfo();

        assertTrue(ethalon.isEqual(sample1));
        assertTrue(ethalon.isEqual(sample2));
        // changing section
        List<DbGridColumn> header = new ArrayList<>();
        DbGridColumn col = new DbGridColumn();
        col.setTitle("sample column for grid design info test");
        header.add(col);

        DbGridRowsColumnsDesignInfo rowsDesign = new DbGridRowsColumnsDesignInfo();
        DbGridTreeDesignInfo treeDesign = new DbGridTreeDesignInfo();
        treeDesign.setParametersSetupScript2GetChildren("sample parameters link script function");
        rowsDesign.setFixedColumns(78);

        sample1.setFont(Font.getFont(Font.SANS_SERIF));
        sample1.setHeader(header);
        sample1.setRowsColumnsDesignInfo(rowsDesign);
        sample1.setTreeDesignInfo(treeDesign);
        //
        sample2.assign(sample1);
        // comparing section
        assertEquals(sample1.getFont(), Font.getFont(Font.SANS_SERIF));
        assertEquals(sample1.getHeader().size(), header.size());
        for(int i=0;i<sample1.getHeader().size();i++)
        {
            DbGridColumn lcol = sample1.getHeader().get(i);
            assertTrue(lcol.isEqual(header.get(i)));
        }
        assertTrue(sample1.getRowsColumnsDesignInfo().isEqual(rowsDesign));
        assertTrue(sample1.getTreeDesignInfo().isEqual(treeDesign));
        //
        assertTrue(sample1.isEqual(sample2));

        assertFalse(ethalon.isEqual(sample1));
        assertFalse(ethalon.isEqual(sample2));
        sample2.assign(ethalon);

        assertTrue(ethalon.isEqual(sample2));
    }

}
