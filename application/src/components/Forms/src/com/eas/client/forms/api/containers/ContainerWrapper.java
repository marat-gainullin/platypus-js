/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Container;
import com.eas.controls.layouts.box.BoxLayout;
import com.eas.controls.layouts.margin.MarginLayout;
import com.eas.controls.wrappers.ButtonGroupWrapper;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

/**
 *
 * @author mg
 */
public class ContainerWrapper {

    public static Container<?> wrap(JPanel aDelegate, LayoutManager aLayout) {
        if (aLayout instanceof MarginLayout) {
            return new AnchorsPane(aDelegate);
        } else if (aLayout instanceof BorderLayout) {
            return new BorderPane(aDelegate);
        } else if (aLayout instanceof CardLayout) {
            return new CardPane(aDelegate);
        } else if (aLayout instanceof BoxLayout) {
            return new BoxPane(aDelegate);
        } else if (aLayout instanceof GridLayout) {
            return new GridPane(aDelegate);
        } else if (aLayout instanceof FlowLayout) {
            return new FlowPane(aDelegate);
        } else if (aLayout == null) {
            return new AbsolutePane(aDelegate);
        } else {
            throw new IllegalArgumentException(String.format("Unsupported layout found: %s", aLayout.getClass().getName()));
        }
    }

    public static ButtonGroup wrap(ButtonGroupWrapper aDelegate) {
        return new ButtonGroup(aDelegate);
    }

    public static ScrollPane wrap(JScrollPane aDelegate) {
        return new ScrollPane(aDelegate);
    }

    public static SplitPane wrap(JSplitPane aDelegate) {
        return new SplitPane(aDelegate);
    }

    public static TabbedPane wrap(JTabbedPane aDelegate) {
        return new TabbedPane(aDelegate);
    }

    public static ToolBar wrap(JToolBar aDelegate) {
        return new ToolBar(aDelegate);
    }
}
