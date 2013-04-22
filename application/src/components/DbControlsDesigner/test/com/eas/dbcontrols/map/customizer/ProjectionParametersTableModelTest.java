/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer;

import java.util.List;
import javax.swing.undo.UndoableEditSupport;
import org.geotools.parameter.Parameter;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchIdentifierException;
import org.opengis.referencing.operation.MathTransformFactory;

/**
 *
 * @author pk
 */
public class ProjectionParametersTableModelTest
{
    private MathTransformFactory mtFactory;
    private ParameterValueGroup parameters;
    private ProjectionParametersTableModel model;

    public ProjectionParametersTableModelTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp() throws NoSuchIdentifierException
    {
        mtFactory = ReferencingFactoryFinder.getMathTransformFactory(null);
        parameters = mtFactory.getDefaultParameters("Orthographic");
        model = new ProjectionParametersTableModel(new UndoableEditSupport());
        model.setParameters(parameters);
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of getRowCount method, of class ProjectionParametersTableModel.
     */
    @Test
    public void testGetRowCount()
    {
        System.out.println("getRowCount");
        assertEquals(parameters.values().size(), model.getRowCount());
        model.setParameters(null);
        assertEquals(0, model.getRowCount());
    }

    /**
     * Test of getColumnCount method, of class ProjectionParametersTableModel.
     */
    @Test
    public void testGetColumnCount()
    {
        System.out.println("getColumnCount");
        assertEquals(2, model.getColumnCount());
    }

    /**
     * Test of getValueAt method, of class ProjectionParametersTableModel.
     */
    @Test
    public void testGetValueAt()
    {
        System.out.println("getValueAt");
        checkRangesControl(model);
        final List<GeneralParameterValue> defaultValuesList = parameters.values();
        for (int i = 0; i < defaultValuesList.size(); i++)
        {
            assertEquals(defaultValuesList.get(i).getDescriptor().getName(), model.getValueAt(i, 0));
            assertEquals(defaultValuesList.get(i), model.getValueAt(i, 1));
        }
        model.setParameters(null);
        assertNull(model.getValueAt(0, 0));
    }

    private void checkRangesControl(ProjectionParametersTableModel model)
    {
        try
        {
            Object value = model.getValueAt(-1, 0);
            fail("Returned value " + value + " for illegal cell (-1, 0)");
        } catch (IllegalArgumentException ex)
        {
            // ok
        }
        try
        {
            Object value = model.getValueAt(100000, 0);
            fail("Returned value " + value + " for illegal cell (100000, 0)");
        } catch (IllegalArgumentException ex)
        {
            // ok
        }
        try
        {
            Object value = model.getValueAt(0, -1);
            fail("Returned value " + value + " for illegal cell (0, -1)");
        } catch (IllegalArgumentException ex)
        {
            // ok
        }
        try
        {
            Object value = model.getValueAt(0, 1000);
            fail("Returned value " + value + " for illegal cell (0, 1000)");
        } catch (IllegalArgumentException ex)
        {
            // ok
        }
    }

    /**
     * Test of getParameters method, of class ProjectionParametersTableModel.
     */
    @Test
    @Ignore
    public void testGetParameters()
    {
        System.out.println("getParameters");
        ProjectionParametersTableModel instance = new ProjectionParametersTableModel(new UndoableEditSupport());
        ParameterValueGroup expResult = null;
        ParameterValueGroup result = instance.getParameters();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setParameters method, of class ProjectionParametersTableModel.
     */
    @Test
    @Ignore
    public void testSetParameters()
    {
        System.out.println("setParameters");
        ParameterValueGroup parameters1 = null;
        ProjectionParametersTableModel instance = new ProjectionParametersTableModel(new UndoableEditSupport());
        instance.setParameters(parameters1);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isCellEditable method, of class ProjectionParametersTableModel.
     */
    @Test
    public void testIsCellEditable()
    {
        System.out.println("isCellEditable");
        try
        {
            Object value = model.isCellEditable(-1, 0);
            fail("Returned value " + value + " for illegal cell (-1, 0)");
        } catch (IllegalArgumentException ex)
        {
            // ok
        }
        try
        {
            Object value = model.isCellEditable(100000, 0);
            fail("Returned value " + value + " for illegal cell (100000, 0)");
        } catch (IllegalArgumentException ex)
        {
            // ok
        }
        try
        {
            Object value = model.isCellEditable(0, -1);
            fail("Returned value " + value + " for illegal cell (0, -1)");
        } catch (IllegalArgumentException ex)
        {
            // ok
        }
        try
        {
            Object value = model.isCellEditable(0, 1000);
            fail("Returned value " + value + " for illegal cell (0, 1000)");
        } catch (IllegalArgumentException ex)
        {
            // ok
        }
        for (int i = 0; i < parameters.values().size(); i++)
        {
            assertFalse(model.isCellEditable(i, 0));
            assertEquals(parameters.values().get(i) instanceof Parameter, model.isCellEditable(i, 1));
        }
        model.setParameters(null);
        assertFalse(model.isCellEditable(0, 0));
        assertFalse(model.isCellEditable(0, 1));
    }

    /**
     * Test of setValueAt method, of class ProjectionParametersTableModel.
     */
    @Test
    public void testSetValueAt()
    {
        System.out.println("setValueAt");
        checkRangesControl(model);
        for (int i = 0; i < parameters.values().size(); i++)
            if (parameters.values().get(i) instanceof Parameter)
            {
                final Parameter q = (Parameter) parameters.values().get(i);
                model.setValueAt(85.0, i, 1);
                assertEquals(85, q.intValue());
            }
    }

    /**
     * Test of getColumnName method, of class ProjectionParametersTableModel.
     */
    @Test
    public void testGetColumnName()
    {
        System.out.println("getColumnName");
        try
        {
            Object value = model.getColumnName(-1);
            fail("Returned value " + value + " for illegal column -1");
        } catch (IllegalArgumentException ex)
        {
            // ok
        }
        try
        {
            Object value = model.getColumnName(100000);
            fail("Returned value " + value + " for illegal cell (100000, 0)");
        } catch (IllegalArgumentException ex)
        {
            // ok
        }
        assertEquals("Parameter", model.getColumnName(0));
        assertEquals("Value", model.getColumnName(1));
    }
}
