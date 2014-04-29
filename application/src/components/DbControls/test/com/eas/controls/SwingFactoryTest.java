/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls;

import com.eas.client.settings.SettingsConstants;
import com.eas.controls.visitors.SwingFactory;
import com.eas.store.Object2Dom;
import com.eas.util.BinaryUtils;
import com.eas.xml.dom.Source2XmlDom;
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import static org.junit.Assert.*;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class SwingFactoryTest {

    public FormDesignInfo readForm(String formResourceName) throws IOException {
        try (InputStream is = SwingFactoryTest.class.getResourceAsStream(formResourceName)) {
            byte[] bfdata = BinaryUtils.readStream(is, -1);
            String sfdata = new String(bfdata, SettingsConstants.COMMON_ENCODING);
            Document fdoc = Source2XmlDom.transform(sfdata);
            FormDesignInfo form = new FormDesignInfo();
            Object2Dom.transform(form, fdoc);
            return form;
        }
    }

    private JFrame displayAsFrame(FormDesignInfo form) {
        SwingFactory factory = new SwingFactory();
        form.accept(factory);
        JPanel rootContainer = factory.getResult();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(rootContainer, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    @Test
    public void layoutsContainersControlsTest() throws Exception {
        FormDesignInfo form = null;
        form = readForm("resources/layoutsContainersControls.xml");
        JFrame frame = displayAsFrame(form);
        // asserts section
        assertNull(frame.getJMenuBar());
        JPanel readContainer = (JPanel)frame.getContentPane().getComponent(0);
        assertTrue(readContainer.getLayout() instanceof GroupLayout);
        assertTrue(readContainer.getComponentCount() == 33);
        frame.dispose();
    }

    @Test
    public void sameSizeGroupLayoutTest() throws Exception {
        FormDesignInfo form = null;
        form = readForm("resources/sameSizeGroupLayout.xml");
        JFrame frame = displayAsFrame(form);
        // asserts section
        assertNull(frame.getJMenuBar());
        JPanel readContainer = (JPanel)frame.getContentPane().getComponent(0);
        assertTrue(readContainer.getLayout() instanceof GroupLayout);
        assertTrue(readContainer.getComponentCount() == 4);
        assertEquals(readContainer.getComponent(0).getWidth(), readContainer.getComponent(1).getWidth());
        assertEquals(readContainer.getComponent(2).getHeight(), readContainer.getComponent(3).getHeight());
        frame.dispose();
    }

    @Test
    public void alignBottomTest() throws Exception {
        FormDesignInfo form = null;
        form = readForm("resources/alignTestBottom.xml");
        JFrame frame = displayAsFrame(form);
        // asserts section
        assertNull(frame.getJMenuBar());
        JPanel readContainer = (JPanel)frame.getContentPane().getComponent(0);
        assertTrue(readContainer.getLayout() instanceof GroupLayout);
        assertTrue(readContainer.getComponentCount() == 3);
        frame.dispose();
    }

    @Test
    public void alignCenterHorizontallyTest() throws Exception {
        FormDesignInfo form = null;
        form = readForm("resources/alignTestCenterHorizontally.xml");
        JFrame frame = displayAsFrame(form);
        // asserts section
        assertNull(frame.getJMenuBar());
        JPanel readContainer = (JPanel)frame.getContentPane().getComponent(0);
        assertTrue(readContainer.getLayout() instanceof GroupLayout);
        assertTrue(readContainer.getComponentCount() == 3);
        frame.dispose();
    }

    @Test
    public void alignCenterVerticallyTest() throws Exception {
        FormDesignInfo form = null;
        form = readForm("resources/alignTestCenterVertically.xml");
        JFrame frame = displayAsFrame(form);
        // asserts section
        assertNull(frame.getJMenuBar());
        JPanel readContainer = (JPanel)frame.getContentPane().getComponent(0);
        assertTrue(readContainer.getLayout() instanceof GroupLayout);
        assertTrue(readContainer.getComponentCount() == 3);
        frame.dispose();
    }

    @Test
    public void alignLeftTest() throws Exception {
        FormDesignInfo form = null;
        form = readForm("resources/alignTestLeft.xml");
        JFrame frame = displayAsFrame(form);
        // asserts section
        assertNull(frame.getJMenuBar());
        JPanel readContainer = (JPanel)frame.getContentPane().getComponent(0);
        assertTrue(readContainer.getLayout() instanceof GroupLayout);
        assertTrue(readContainer.getComponentCount() == 3);
        frame.dispose();
    }

    @Test
    public void alignRightTest() throws Exception {
        FormDesignInfo form = null;
        form = readForm("resources/alignTestRight.xml");
        JFrame frame = displayAsFrame(form);
        // asserts section
        assertNull(frame.getJMenuBar());
        JPanel readContainer = (JPanel)frame.getContentPane().getComponent(0);
        assertTrue(readContainer.getLayout() instanceof GroupLayout);
        assertTrue(readContainer.getComponentCount() == 3);
        frame.dispose();
    }

    @Test
    public void alignTopTest() throws Exception {
        FormDesignInfo form = null;
        form = readForm("resources/alignTestTop.xml");
        JFrame frame = displayAsFrame(form);
        // asserts section
        assertNull(frame.getJMenuBar());
        JPanel readContainer = (JPanel)frame.getContentPane().getComponent(0);
        assertTrue(readContainer.getLayout() instanceof GroupLayout);
        assertTrue(readContainer.getComponentCount() == 3);
        frame.dispose();
    }
}
