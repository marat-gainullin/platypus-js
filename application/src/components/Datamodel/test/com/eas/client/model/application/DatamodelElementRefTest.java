/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.application;

import com.eas.client.model.ModelElementRef;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.model.ModelElementRef;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
/**
 *
 * @author mg
 */
public class DatamodelElementRefTest {

    @Test
    public void assignEqualsTest()
    {
        ModelElementRef ethalon = new ModelElementRef();
        ModelElementRef sample1 = new ModelElementRef();
        ModelElementRef sample2 = new ModelElementRef();

        assertEquals(ethalon, sample1);
        assertEquals(ethalon, sample2);
        // changing section
        Long entityId = IDGenerator.genID();
        sample1.setEntityId(entityId);
        sample1.setField(false);
        sample1.setFieldName("sampleFieldName");
        //
        sample2.assign(sample1);
        // comparing section
        assertEquals(sample1.getEntityId(), entityId);
        assertEquals(sample1.isField(), false);
        assertEquals(sample1.getFieldName(), "sampleFieldName");
        //
        assertEquals(sample1, sample2);

        assertFalse(ethalon.equals(sample1));
        assertFalse(ethalon.equals(sample2));
        sample2.assign(ethalon);

        assertEquals(ethalon, sample2);
    }

}
