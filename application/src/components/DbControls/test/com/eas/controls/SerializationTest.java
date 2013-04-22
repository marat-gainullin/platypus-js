/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls;

import java.awt.Insets;
import com.eas.controls.containers.PanelDesignInfo;
import com.eas.controls.layouts.BorderLayoutDesignInfo;
import com.eas.controls.layouts.constraints.BorderLayoutConstraintsDesignInfo;
import com.eas.controls.layouts.constraints.GridBagLayoutConstraintsDesignInfo;
import com.eas.controls.plain.CheckDesignInfo;
import com.eas.controls.plain.RadioDesignInfo;
import com.eas.controls.plain.ToggleButtonDesignInfo;
import com.eas.store.Object2Dom;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import javax.swing.WindowConstants;
import org.junit.Test;
import org.w3c.dom.Document;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class SerializationTest {

    @Test
    public void generalTest() {
        FormDesignInfo form = new FormDesignInfo();
        form.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        form.setIconImage("sample.png");
        Document doc = Object2Dom.transform(form, "form");
        String serialized = XmlDom2String.transform(doc);
        FormDesignInfo dform = new FormDesignInfo();
        Document ddoc = Source2XmlDom.transform(serialized);
        Object2Dom.transform(dform, ddoc);
        assertEquals(form.getDefaultCloseOperation(), dform.getDefaultCloseOperation());
        assertEquals(form.getIconImage(), dform.getIconImage());
    }

    @Test
    public void childrenTest() {
        FormDesignInfo form = new FormDesignInfo();
        RadioDesignInfo rdInfo = new RadioDesignInfo();
        rdInfo.setDisabledIcon("disabled.png");
        rdInfo.setSelected(true);
        ToggleButtonDesignInfo tbInfo = new ToggleButtonDesignInfo();
        tbInfo.setText("sampleButton");
        tbInfo.setToolTipText("sampleButton's tooltip");
        CheckDesignInfo chInfo = new CheckDesignInfo();
        chInfo.setBorderPaintedFlat(true);
        chInfo.setSelected(true);
        chInfo.setSelectedIcon("selectedIcon.png");

        form.getChildren().add(rdInfo);
        form.getChildren().add(tbInfo);
        form.getChildren().add(chInfo);

        Document doc = Object2Dom.transform(form, "form");
        String serialized = XmlDom2String.transform(doc);
        FormDesignInfo dform = new FormDesignInfo();
        Document ddoc = Source2XmlDom.transform(serialized);
        Object2Dom.transform(dform, ddoc);
        assertEquals(form.getChildren().size(), dform.getChildren().size());
        for (int i = 0; i < form.getChildren().size(); i++) {
            assertTrue(form.getChildren().get(i).isEqual(dform.getChildren().get(i)));
        }
    }

    @Test
    public void layoutPolymorphismTest() {
        FormDesignInfo form = new FormDesignInfo();
        RadioDesignInfo rdInfo = new RadioDesignInfo();
        rdInfo.setDisabledIcon("disabled.png");
        rdInfo.setSelected(true);
        PanelDesignInfo pInfo = new PanelDesignInfo();
        pInfo.setName("rootPane");
        rdInfo.setParent("rootPane");
        form.getChildren().add(rdInfo);
        form.getChildren().add(pInfo);
        BorderLayoutDesignInfo bLayout = new BorderLayoutDesignInfo();
        bLayout.setHgap(20);
        bLayout.setVgap(56);
        pInfo.setLayout(bLayout);
        Document doc = Object2Dom.transform(form, "form");
        String serialized = XmlDom2String.transform(doc);
        FormDesignInfo dform = new FormDesignInfo();
        Document ddoc = Source2XmlDom.transform(serialized);
        Object2Dom.transform(dform, ddoc);
        assertEquals(2, dform.getChildren().size());
        assertTrue(rdInfo.isEqual(dform.getChildren().get(0)));
        assertTrue(pInfo.isEqual(dform.getChildren().get(1)));
        assertEquals("rootPane", ((RadioDesignInfo) dform.getChildren().get(0)).getParent());
        assertEquals("rootPane", ((PanelDesignInfo) dform.getChildren().get(1)).getName());
        assertTrue(((PanelDesignInfo) dform.getChildren().get(1)).getLayout() instanceof BorderLayoutDesignInfo);
    }

    @Test
    public void layoutConstraintsPolymorphismTest() {
        FormDesignInfo form = new FormDesignInfo();
        RadioDesignInfo rbInfo = new RadioDesignInfo();
        rbInfo.setDisabledIcon("enabled.png");
        rbInfo.setSelected(true);
        BorderLayoutConstraintsDesignInfo bContraints = new BorderLayoutConstraintsDesignInfo();
        rbInfo.setConstraints(bContraints);
        ToggleButtonDesignInfo tbInfo = new ToggleButtonDesignInfo();
        tbInfo.setText("sampleButton_4");
        tbInfo.setToolTipText("sampleB_0900_utton's tooltip");
        GridBagLayoutConstraintsDesignInfo gConstraints = new GridBagLayoutConstraintsDesignInfo();
        gConstraints.setGridx(45);
        gConstraints.setGridwidth(3);
        gConstraints.setInsets(new Insets(7, 5, 9, 3));
        tbInfo.setConstraints(gConstraints);
        form.getChildren().add(rbInfo);
        form.getChildren().add(tbInfo);
        Document doc = Object2Dom.transform(form, "form");
        String serialized = XmlDom2String.transform(doc);
        FormDesignInfo dform = new FormDesignInfo();
        Document ddoc = Source2XmlDom.transform(serialized);
        Object2Dom.transform(dform, ddoc);
        assertEquals(2, dform.getChildren().size());
        assertTrue(rbInfo.isEqual(dform.getChildren().get(0)));
        assertTrue(tbInfo.isEqual(dform.getChildren().get(1)));
        assertTrue(((RadioDesignInfo)dform.getChildren().get(0)).getConstraints() instanceof BorderLayoutConstraintsDesignInfo);
        assertTrue(((ToggleButtonDesignInfo)dform.getChildren().get(1)).getConstraints() instanceof GridBagLayoutConstraintsDesignInfo);
        assertTrue(((RadioDesignInfo)dform.getChildren().get(0)).getConstraints().isEqual(rbInfo.getConstraints()));
        assertTrue(((ToggleButtonDesignInfo)dform.getChildren().get(1)).getConstraints().isEqual(tbInfo.getConstraints()));
    }
}
