/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.gui.grid.header;

import com.eas.gui.CascadedStyle;
import java.awt.Color;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class GridColumnsTest {

    @Test
    public void equalsAssignTest()
    {
        GridColumnsGroup col = new GridColumnsGroup();
        GridColumnsGroup col1 = new GridColumnsGroup();
        GridColumnsGroup childCol = new GridColumnsGroup();

        assertTrue(col.isEqual(col1));

        col.setBackground(Color.darkGray);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setForeground(Color.darkGray);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setEditable(!col.isEditable());
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setEnabled(!col.isEnabled());
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setName("some name");
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setPlain(!col.isPlain());
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setReadonly(!col.isReadonly());
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setSelectOnly(!col.isSelectOnly());
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setSubstitute(!col.isSubstitute());
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setTitle("some title");
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setVisible(!col.isVisible());
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setMinWidth(345);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setMaxWidth(345);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setStyle(new CascadedStyle());
        col.getStyle().setBackground(Color.darkGray);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.addChild(childCol);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));
    }
}
