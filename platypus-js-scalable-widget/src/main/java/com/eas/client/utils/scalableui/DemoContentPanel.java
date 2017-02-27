package com.eas.client.utils.scalableui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultDesktopManager;
import javax.swing.DropMode;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Marat
 */
public class DemoContentPanel extends JPanel{

    private class LModel extends Object implements ListModel{

        Set<ListDataListener> listners = new HashSet<>();
        
        public int getSize() {
            return 269;
        }

        public Object getElementAt(int index) {
            return "Element_"+String.valueOf(index);
        }

        public void addListDataListener(ListDataListener l) {
            listners.add(l);
        }

        public void removeListDataListener(ListDataListener l) {
            listners.remove(l);
        }
        
    }
    
    private class SampleComboModel implements ComboBoxModel{

        HashSet<ListDataListener> list = new HashSet<ListDataListener>(); 
        String[] samItems = new String[]{"Item1", "Item2", "Item3", "Item4", "Item5", "Item6",
                                         "Item1", "Item2", "Item3", "Item4", "Item5", "Item6",
                                         "Item1", "Item2", "Item3", "Item4", "Item5", "Item6",
                                         "Item1", "Item2", "Item3", "Item4", "Item5", "Item6"};
        Object selected = null;
        
        public void setSelectedItem(Object anItem) {
            selected = anItem;
        }

        public Object getSelectedItem() {
            return selected;
        }

        public int getSize() {
            return samItems.length;
        }

        public Object getElementAt(int index) {
            if(index >=0 || index < samItems.length)
                return samItems[index];
            return null;
        }

        public void addListDataListener(ListDataListener l) {
            list.add(l);
        }

        public void removeListDataListener(ListDataListener l) {
            list.remove(l);
        }
        
    }
    
    public DemoContentPanel() {
        setLayout(null);
        JDesktopPane dp = new JDesktopPane();
        dp.setLocation(20, 20);
        dp.setSize(250, 450);
        dp.setDesktopManager(new DefaultDesktopManager(){
            @Override
            public void dragFrame(JComponent f, int newX, int newY) {
                f.setLocation(newX, newY);
            }           
        });
        JInternalFrame ifr = new JInternalFrame("Sample frame", true, true, true, true);
        ifr.setLocation(20, 20);
        ifr.setSize(150, 150);
        //ifr.setBackground(Color.cyan);
        ifr.getContentPane().setLayout(new BorderLayout());
        JTextArea lta = new JTextArea();
        lta.setBackground(Color.yellow);
        lta.setToolTipText("Sample text component hint");
        JScrollPane lsp = new JScrollPane(lta); 
        lta.setFont(Font.decode(Font.DIALOG_INPUT));
        ifr.getContentPane().add(lsp, BorderLayout.CENTER);
        ifr.setVisible(true);
        dp.add(ifr, JLayeredPane.DEFAULT_LAYER);

        JList lst = new JList();
        lst.setModel(new LModel());
        lst.setToolTipText("Sample tooltip. Right click to show scaled popup!");
        lst.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lst.setDragEnabled(true);

        JPopupMenu pmenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("sample popup menu item 1");
        pmenu.add(menuItem);
        menuItem = new JMenuItem("sample popup menu item 2");
        pmenu.add(menuItem);
        lst.setComponentPopupMenu(pmenu);
        
        JList lst1 = new JList();
        lst1.setModel(new LModel());
        lst1.setToolTipText(" tooltip Sample. Right click to show scaled popup!");
        lst1.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        lst1.setDragEnabled(true);
        lst1.setDropMode(DropMode.INSERT);
        lst1.setComponentPopupMenu(pmenu);
        
        JScrollPane sp = new JScrollPane(lst);
        sp.setLocation(290, 20);
        sp.setSize(250, 350);

        JScrollPane sp1 = new JScrollPane(lst1);
        sp1.setLocation(290, 390);
        sp1.setSize(250, 150);
        
        
        JInternalFrame ifr1 = new JInternalFrame("Sample frame 1", true, true, true, true);
        ifr1.getContentPane().setLayout(new GridLayout(5, 2));
        ifr1.setSize(150, 150);
        SampleComboModel lComboModel = new SampleComboModel();
        JComboBox combo1 = new JComboBox(lComboModel);
        JComboBox combo2 = new JComboBox(lComboModel);
        JComboBox combo3 = new JComboBox(lComboModel);
        JComboBox combo4 = new JComboBox(lComboModel);
        JComboBox combo5 = new JComboBox(lComboModel);
        JComboBox combo6 = new JComboBox(lComboModel);
        JComboBox combo7 = new JComboBox(lComboModel);
        JComboBox combo8 = new JComboBox(lComboModel);
        ifr1.add(combo1);
        ifr1.add(combo2);
        ifr1.add(combo3);
        ifr1.add(combo4);
        ifr1.add(combo5);
        ifr1.add(combo6);
        ifr1.add(combo7);
        ifr1.add(combo8);

        ifr1.setVisible(true);
        dp.add(ifr1, JLayeredPane.DEFAULT_LAYER);
        add(sp);
        add(sp1);

        add(dp);
        //setBackground(Color.magenta);
        setPreferredSize(new Dimension(550, 500));
    }
}
