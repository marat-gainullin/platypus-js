/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.threetier.requests;

import com.bearsoft.rowset.exceptions.AlreadyExistSerializerException;
import com.eas.client.threetier.PlatypusRowsetReader;
import com.eas.client.threetier.PlatypusRowsetWriter;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.io.IOException;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class RequestsBaseTest {

    protected static final long postgreSQLInDevelopTest = 128083706440617l;

    protected static final GeometryFactory gFactory = new GeometryFactory();
    protected PlatypusRowsetWriter customWritersContainer = new PlatypusRowsetWriter();
    protected PlatypusRowsetReader customReadersContainer = new PlatypusRowsetReader();

    public RequestsBaseTest() throws IOException
    {
        super();
    }

    @Test
    public void dummyTest()
    {
    }
}
