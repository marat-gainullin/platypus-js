/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.eas.client.DatabaseClient;
import com.eas.client.DbClient;
import com.eas.client.datamodel.ApplicationDbModel;
import com.eas.client.datamodel.ApplicationEntity;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.text.DbTextCustomizer;
import com.eas.store.Object2Dom;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.undo.UndoManager;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class CustomizersTest {

    public static DbClient initDevelopTestClient() throws Exception {
        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl("jdbc:oracle:thin:@asvr/adb");
        Properties info = new Properties();
        info.put("schema", "eas");
        info.put("user", "eas");
        info.put("password", "eas");
        settings.setInfo(info);
        return new DatabaseClient(settings);
    }

    @Test
    public void visualTest() throws Exception {

        final UndoManager editor = new UndoManager();

        DbGridColumn col = new DbGridColumn();
        col.setWidth(1056);
        col.setReadonly(true);

        DbGridColumn child = new DbGridColumn();
        child.setWidth(157);
        child.setReadonly(false);

        DbGridColumn child1 = new DbGridColumn();
        child1.setWidth(506);
        child1.setReadonly(false);

        DbGridColumn child2 = new DbGridColumn();
        child2.setWidth(687);
        child2.setReadonly(true);
        /*
        col.getLinked().put("first", child);
        col.getLinked().put("second", child1);
        col.getLinked().put("third", child2);
         */

        DbGridColumn child3 = new DbGridColumn();
        child.setWidth(157);
        child.setReadonly(false);

        DbGridColumn child4 = new DbGridColumn();
        child1.setWidth(506);
        child1.setReadonly(false);

        DbGridColumn child5 = new DbGridColumn();
        child2.setWidth(687);
        child2.setReadonly(true);

        col.getChildren().add(child3);
        col.getChildren().add(child4);
        col.getChildren().add(child5);

        Document doc = Object2Dom.transform(col, "column");
        String serialized = XmlDom2String.transform(doc);

        col = new DbGridColumn();
        col.setReadonly(true);
        col.setName("");
        col.setWidth(0);
        doc = Source2XmlDom.transform(serialized);
        Object2Dom.transform(col, doc);

        doc = Object2Dom.transform(col, "column");
        String serialized1 = XmlDom2String.transform(doc);
        boolean fine = serialized.equals(serialized1);
        fine = false;

        DbClient client = initDevelopTestClient();
        try {
            ApplicationDbModel dm = new ApplicationDbModel(client);
            ApplicationEntity ent = dm.newGenericEntity();
            ent.setQueryId("4467489525667723800");
            dm.addEntity(ent);

            ent = dm.newGenericEntity();
            ent.setQueryId("2349965815510318600");
            dm.addEntity(ent);

            ent = dm.newGenericEntity();
            ent.setQueryId("3529091781372718100");
            dm.addEntity(ent);

            JFrame fr = new JFrame();
            fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            DbGrid check = new DbGrid();
            //final DbGridCustomizer checkCust = new DbGridCustomizer();
            final DbTextCustomizer checkCust = new DbTextCustomizer();
            //final DbSpinCustomizer checkCust = new DbSpinCustomizer();
            //final DbLabelCustomizer checkCust = new DbLabelCustomizer();
            //final DbSchemeCustomizer checkCust = new DbSchemeCustomizer();
            //final DbImageCustomizer checkCust = new DbImageCustomizer();
            //final DbDateCustomizer checkCust = new DbDateCustomizer();
            //final DbComboCustomizer checkCust = new DbComboCustomizer();
            //final DbCheckCustomizer checkCust = new DbCheckCustomizer();
            //checkCust.setControlsCheckTarget(checkCust);
            //check.setDesignInfo(new DbTextDesignInfo());
            //check.setDesignInfo(new DbSpinDesignInfo());
            //check.setDesignInfo(new DbLabelDesignInfo());
            //check.setDesignInfo(new DbSchemeDesignInfo());
            //check.setDesignInfo(new DbImageDesignInfo());
            //check.setDesignInfo(new DbDateDesignInfo());
            //check.setDesignInfo(new DbComboDesignInfo());
            //check.setDesignInfo(new DbCheckDesignInfo());
            check.setDatamodel(dm);
            checkCust.setObject(check);

            JPanel pnl = new JPanel(new BorderLayout());
            JPanel pnlBtns = new JPanel(new BorderLayout());
            JButton undo = new JButton("undo");
            undo.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editor.canUndo()) {
                        editor.undo();
                    }
                }
            });
            JButton redo = new JButton("redo");
            redo.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editor.canRedo()) {
                        editor.redo();
                    }
                }
            });
            pnlBtns.add(undo, BorderLayout.WEST);
            pnlBtns.add(redo, BorderLayout.EAST);
            pnl.add(pnlBtns, BorderLayout.NORTH);
            pnl.add(checkCust, BorderLayout.CENTER);
            fr.getContentPane().add(pnl);
            fr.setSize(450, 510);
            fr.setVisible(true);
            Thread.sleep(100);
        } finally {
            client.shutdown();
        }
    }
}
