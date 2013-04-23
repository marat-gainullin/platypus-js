/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model;

import java.util.Collection;

import com.eas.client.model.Model;

/**
 *
 * @author mg
 */
public class ModelStructureTest extends ModelBaseTest{

    public void testParametersEntity()
    {
        System.out.println("parametersEntityTest");
        Model dm = new Model();
        assertNotNull(dm.getParametersEntity());
    }

	@Override
    public void validate() throws Exception {
    }

	@Override
    public Collection<String> queries() {
	    return null;
    }

}
