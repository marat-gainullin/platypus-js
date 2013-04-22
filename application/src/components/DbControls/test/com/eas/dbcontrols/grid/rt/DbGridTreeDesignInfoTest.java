/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.rt;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.ModelEntityParameterRef;
import com.eas.dbcontrols.grid.DbGridTreeDesignInfo;
import static org.junit.Assert.*;
import org.junit.Test;
/**
 *
 * @author mg
 */
public class DbGridTreeDesignInfoTest {

    @Test
    public void assignEqualsTest()
    {
        DbGridTreeDesignInfo ethalon = new DbGridTreeDesignInfo();
        DbGridTreeDesignInfo sample1 = new DbGridTreeDesignInfo();
        DbGridTreeDesignInfo sample2 = new DbGridTreeDesignInfo();

        assertTrue(ethalon.isEqual(sample1));
        assertTrue(ethalon.isEqual(sample2));
        // changing section
        ModelEntityParameterRef parToGetChildrenDs = new ModelEntityParameterRef();
        parToGetChildrenDs.setEntityId(IDGenerator.genID());

        ModelElementRef parToGetChildrenSourceDs = new ModelElementRef();
        parToGetChildrenSourceDs.setEntityId(IDGenerator.genID());

        ModelElementRef unaryLinkDs = new ModelElementRef();
        unaryLinkDs.setEntityId(IDGenerator.genID());

        sample1.setParametersSetupScript2GetChildren("scriptChilrenLinkFunction");
        sample1.setTreeKind(DbGridTreeDesignInfo.SCRIPT_PARAMETERS_TREE_KIND);
        sample1.setParam2GetChildren(parToGetChildrenDs);
        sample1.setParamSourceField(parToGetChildrenSourceDs);
        sample1.setUnaryLinkField(unaryLinkDs);
        //
        sample2.assign(sample1);
        // comparing section
        assertEquals(sample1.getParametersSetupScript2GetChildren(), "scriptChilrenLinkFunction");
        assertEquals(sample1.getTreeKind(), DbGridTreeDesignInfo.SCRIPT_PARAMETERS_TREE_KIND);
        assertEquals(sample1.getParam2GetChildren(), parToGetChildrenDs);
        assertEquals(sample1.getParamSourceField(), parToGetChildrenSourceDs);
        assertEquals(sample1.getUnaryLinkField(), unaryLinkDs);
        //
        assertTrue(sample1.isEqual(sample2));

        assertFalse(ethalon.isEqual(sample1));
        assertFalse(ethalon.isEqual(sample2));
        sample2.assign(ethalon);

        assertTrue(ethalon.isEqual(sample2));
    }

}
