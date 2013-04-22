/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.gui;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Color;
import javax.swing.SwingConstants;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class StyleTest {

    @Test
    public void defaultsTest() {
        CascadedStyle s = new CascadedStyle();
        assertNotNull(s.getAlign());
        assertNotNull(s.getFont());
        assertNotNull(s.getBackground());
        assertNotNull(s.getForeground());
        assertNull(s.getIcon());
        assertNull(s.getFolderIcon());
        assertNull(s.getOpenFolderIcon());
        assertNull(s.getLeafIcon());
    }

    @Test
    public void equalsAssignTest() {
        CascadedStyle s = new CascadedStyle();
        CascadedStyle s1 = new CascadedStyle();

        s.setAlign(SwingConstants.CENTER);
        assertFalse(s.isEqual(s1));
        s.assign(s1);
        assertTrue(s.isEqual(s1));

        s.setFont(new Font(s.getFont().getFamily(), s.getFont().getStyle(), s.getFont().getSize() + 1));
        assertFalse(s.isEqual(s1));
        s.assign(s1);
        assertTrue(s.isEqual(s1));

        s.setBackground(Color.darkGray);
        assertFalse(s.isEqual(s1));
        s.assign(s1);
        assertTrue(s.isEqual(s1));

        s.setForeground(Color.darkGray);
        assertFalse(s.isEqual(s1));
        s.assign(s1);
        assertTrue(s.isEqual(s1));

        s.setIconName("iconName");
        assertFalse(s.isEqual(s1));
        s.assign(s1);
        assertTrue(s.isEqual(s1));

        s.setFolderIconName("folderIconName");
        assertFalse(s.isEqual(s1));
        s.assign(s1);
        assertTrue(s.isEqual(s1));

        s.setOpenFolderIconName("openFolderIconName");
        assertFalse(s.isEqual(s1));
        s.assign(s1);
        assertTrue(s.isEqual(s1));

        s.setLeafIconName("leafIconName");
        assertFalse(s.isEqual(s1));
        s.assign(s1);
        assertTrue(s.isEqual(s1));
    }

    @Test
    public void cascadeTest() {
        CascadedStyle s = new CascadedStyle();

        Icon icon = new ImageIcon();
        Icon folderIcon = new ImageIcon();
        Icon openFolderIcon = new ImageIcon();
        Icon leafIcon = new ImageIcon();

        s.setAlign(SwingConstants.CENTER);
        s.setFont(new Font(s.getFont().getFamily(), s.getFont().getStyle(), s.getFont().getSize() + 1));
        s.setBackground(Color.darkGray);
        s.setForeground(Color.darkGray);
        s.setIcon(icon);
        s.setFolderIcon(folderIcon);
        s.setOpenFolderIcon(openFolderIcon);
        s.setLeafIcon(leafIcon);

        CascadedStyle s1 = new CascadedStyle();
        s1.setAlign(null);
        s1.setFont(null);
        s1.setBackground(null);
        s1.setForeground(null);

        assertSame(s1.getAlign(), CascadedStyle.defaultAlign());
        assertSame(s1.getFont(), CascadedStyle.defaultFont());
        assertSame(s1.getBackground(), CascadedStyle.defaultBackground());
        assertSame(s1.getForeground(), CascadedStyle.defaultForeground());
        assertNull(s1.getIcon());
        assertNull(s1.getFolderIcon());
        assertNull(s1.getOpenFolderIcon());
        assertNull(s1.getLeafIcon());

        s1.setParent(s);
        assertNotSame(s1.getAlign(), CascadedStyle.defaultAlign());
        assertNotSame(s1.getFont(), CascadedStyle.defaultFont());
        assertNotSame(s1.getBackground(), CascadedStyle.defaultBackground());
        assertNotSame(s1.getForeground(), CascadedStyle.defaultForeground());
        assertNotNull(s1.getIcon());
        assertNotNull(s1.getFolderIcon());
        assertNotNull(s1.getOpenFolderIcon());
        assertNotNull(s1.getLeafIcon());
    }
}
