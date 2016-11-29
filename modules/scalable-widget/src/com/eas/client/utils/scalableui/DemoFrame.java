/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.utils.scalableui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 *
 * @author Mg
 */
public class DemoFrame extends JFrame{

    public DemoFrame()
    {
        super();
/*
        Container cntr = getContentPane();
        cntr.setLayout(new BorderLayout());
        DemoContentPanel dcp = new DemoContentPanel();
        JXTransformer lDemoScalePanel = new JXTransformer(dcp);
        lDemoScalePanel.rotate(Math.PI/10);
        cntr.add(lDemoScalePanel, BorderLayout.CENTER);
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
 */
        Container cntr = getContentPane();
        cntr.setLayout(new BorderLayout());
        JScalableScrollPane lDemoScalePanel = new JScalableScrollPane();
        DemoContentPanel dcp = new DemoContentPanel();
        lDemoScalePanel.setViewContent(dcp);
        cntr.add(lDemoScalePanel, BorderLayout.CENTER);     
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
}
