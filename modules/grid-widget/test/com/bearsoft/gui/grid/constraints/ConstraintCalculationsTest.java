/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.gui.grid.constraints;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class ConstraintCalculationsTest {

    @Test
    public void windowTest()
    {
        int windowMin = 1;
        int windowMax = 3;
        LinearConstraint window = new LinearConstraint(windowMin, windowMax);
        assertEquals(0, window.constraint(0));
        assertEquals(0, window.constraint(1));//
        assertEquals(1, window.constraint(2));
        assertEquals(2, window.constraint(3));//
        assertEquals(2, window.constraint(4));
        assertEquals(2, window.constraint(5));
    }

    @Test
    public void infiniteLeftTest()
    {
        int windowMin = 0;
        int windowMax = 3;
        LinearConstraint window = new LinearConstraint(windowMin, windowMax);
        assertEquals(0, window.constraint(0));
        assertEquals(1, window.constraint(1));
        assertEquals(2, window.constraint(2));
        assertEquals(3, window.constraint(3));//
        assertEquals(3, window.constraint(4));
        assertEquals(3, window.constraint(5));
    }

    @Test
    public void infiniteRightTest()
    {
        int windowMin = 1;
        int windowMax = Integer.MAX_VALUE;
        LinearConstraint window = new LinearConstraint(windowMin, windowMax);
        assertEquals(0, window.constraint(0));
        assertEquals(0, window.constraint(1));//
        assertEquals(1, window.constraint(2));
        assertEquals(2, window.constraint(3));
        assertEquals(3, window.constraint(4));
        assertEquals(4, window.constraint(5));
    }

    @Test
    public void windowReverseTest()
    {
        int windowMin = 1;
        int windowMax = 3;
        LinearConstraint window = new LinearConstraint(windowMin, windowMax);
        assertEquals(1, window.unconstraint(0));//
        assertTrue(window.inConstraint(window.unconstraint(0)));
        assertEquals(2, window.unconstraint(1));
        assertTrue(window.inConstraint(window.unconstraint(1)));
        assertEquals(3, window.unconstraint(2));//
        assertTrue(window.inConstraint(window.unconstraint(2)));
        assertEquals(4, window.unconstraint(3));
        assertFalse(window.inConstraint(window.unconstraint(3)));
    }

    @Test
    public void infiniteLeftReverseTest()
    {
        int windowMin = 0;
        int windowMax = 3;
        LinearConstraint window = new LinearConstraint(windowMin, windowMax);
        assertEquals(0, window.unconstraint(0));//
        assertTrue(window.inConstraint(window.unconstraint(0)));
        assertEquals(1, window.unconstraint(1));
        assertTrue(window.inConstraint(window.unconstraint(1)));
        assertEquals(2, window.unconstraint(2));//
        assertTrue(window.inConstraint(window.unconstraint(2)));
        assertEquals(3, window.unconstraint(3));
        assertTrue(window.inConstraint(window.unconstraint(3)));
        assertEquals(4, window.unconstraint(4));
        assertFalse(window.inConstraint(window.unconstraint(4)));
    }

    @Test
    public void infiniteRightReverseTest()
    {
        int windowMin = 1;
        int windowMax = Integer.MAX_VALUE;
        LinearConstraint window = new LinearConstraint(windowMin, windowMax);
        assertEquals(1, window.unconstraint(0));//
        assertTrue(window.inConstraint(window.unconstraint(0)));
        assertEquals(2, window.unconstraint(1));
        assertTrue(window.inConstraint(window.unconstraint(1)));
        assertEquals(3, window.unconstraint(2));//
        assertTrue(window.inConstraint(window.unconstraint(2)));
        assertEquals(4, window.unconstraint(3));
        assertTrue(window.inConstraint(window.unconstraint(3)));
        assertEquals(5, window.unconstraint(4));
        assertTrue(window.inConstraint(window.unconstraint(4)));
        assertEquals(6, window.unconstraint(5));
        assertTrue(window.inConstraint(window.unconstraint(5)));
    }

    @Test
    public void invalidConstraintTest()
    {
        LinearConstraint nullWindow = new LinearConstraint(0, -1);
        assertFalse(nullWindow.isValid());
        assertEquals(6, nullWindow.constraint(6));
        assertEquals(4, nullWindow.unconstraint(4));
        nullWindow.setMax(nullWindow.getMax()+1);
        assertTrue(nullWindow.isValid());
        assertEquals(0, nullWindow.constraint(0));
        assertEquals(0, nullWindow.constraint(1));
        assertEquals(0, nullWindow.constraint(2));
        assertEquals(0, nullWindow.unconstraint(0));
        assertEquals(1, nullWindow.unconstraint(1));
    }
}
