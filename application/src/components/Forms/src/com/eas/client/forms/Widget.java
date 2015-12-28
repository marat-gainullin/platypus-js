/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.forms.containers.AnchorsPane;
import com.eas.client.forms.containers.BorderPane;
import com.eas.client.forms.containers.BoxPane;
import com.eas.client.forms.containers.FlowPane;
import com.eas.client.forms.layouts.BoxLayout;
import com.eas.client.forms.layouts.MarginLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 *
 * @author Марат
 */
public interface Widget {

    public static void setWidth(JComponent aTarget, int aValue) {
        if (aTarget.getParent() != null) {
            if (aTarget.getParent() instanceof AnchorsPane || aTarget.getParent().getLayout() instanceof MarginLayout) {
                MarginLayout.ajustWidth(aTarget, aValue);
            } else if (aTarget.getParent() instanceof JViewport && aTarget.getParent().getParent() instanceof JScrollPane) {
                aTarget.setPreferredSize(new Dimension(aValue, aTarget.getHeight()));
            } else if (aTarget.getParent() instanceof BoxPane || aTarget.getParent().getLayout() instanceof BoxLayout) {
                aTarget.setPreferredSize(new Dimension(aValue, aTarget.getHeight()));
            } else if (aTarget.getParent() instanceof BorderPane || aTarget.getParent().getLayout() instanceof BorderLayout) {
                aTarget.setPreferredSize(new Dimension(aValue, aTarget.getHeight()));
            } else if (aTarget.getParent() instanceof FlowPane || aTarget.getParent().getLayout() instanceof FlowLayout) {
                aTarget.setPreferredSize(new Dimension(aValue, aTarget.getHeight()));
            }
        }
        aTarget.setSize(aValue, aTarget.getHeight());
    }

    public static void setHeight(JComponent aTarget, int aValue) {
        if (aTarget.getParent() != null) {
            if (aTarget.getParent() instanceof AnchorsPane || aTarget.getParent().getLayout() instanceof MarginLayout) {
                MarginLayout.ajustHeight(aTarget, aValue);
            } else if (aTarget.getParent() instanceof JViewport && aTarget.getParent().getParent() instanceof JScrollPane) {
                aTarget.setPreferredSize(new Dimension(aTarget.getWidth(), aValue));
            } else if (aTarget.getParent() instanceof BoxPane || aTarget.getParent().getLayout() instanceof BoxLayout) {
                aTarget.setPreferredSize(new Dimension(aTarget.getWidth(), aValue));
            } else if (aTarget.getParent() instanceof BorderPane || aTarget.getParent().getLayout() instanceof BorderLayout) {
                aTarget.setPreferredSize(new Dimension(aTarget.getWidth(), aValue));
            } else if (aTarget.getParent() instanceof FlowPane || aTarget.getParent().getLayout() instanceof FlowLayout) {
                aTarget.setPreferredSize(new Dimension(aTarget.getWidth(), aValue));
            }
        }
        aTarget.setSize(aTarget.getWidth(), aValue);
    }

    public static final String PARENT_JSDOC = ""
            + "/**\n"
            + " * Parent container of this widget.\n"
            + " */";

    public Widget getParentWidget();

    public static final String LEFT_JSDOC = ""
            + "/**\n"
            + " * Horizontal coordinate of the component.\n"
            + " */";

    public int getLeft();

    public void setLeft(int aValue);

    public static final String TOP_JSDOC = ""
            + "/**\n"
            + " * Vertical coordinate of the component.\n"
            + " */";

    public int getTop();

    public void setTop(int aValue);

    public static final String WIDTH_JSDOC = ""
            + "/**\n"
            + " * Width of the component.\n"
            + " */";

    public int getWidth();

    public void setWidth(int aValue);

    public static final String HEIGHT_JSDOC = ""
            + "/**\n"
            + " * Height of the component.\n"
            + " */";

    public int getHeight();

    public void setHeight(int aValue);

    public static final String GET_NEXT_FOCUSABLE_COMPONENT_JSDOC = ""
            + "/**\n"
            + " * Overrides the default focus traversal policy for this component's focus traversal cycle"
            + " by unconditionally setting the specified component as the next component in the cycle,"
            + " and this component as the specified component's previous component.\n"
            + " */";

    public JComponent getNextFocusableComponent();

    public void setNextFocusableComponent(JComponent aValue);

    public static final String ERROR_JSDOC = ""
            + "/**\n"
            + " * An error message of this component.\n"
            + " * Validation procedure may set this property and subsequent focus lost event will clear it.\n"
            + " */";

    public String getError();

    public void setError(String aValue);
    public static final String BACKGROUND_JSDOC = ""
            + "/**\n"
            + " * The background color of this component.\n"
            + " */";

    public static final String FOCUS_JSDOC = ""
            + "/**\n"
            + " * Tries to acquire focus for this component.\n"
            + " */";

    public void focus();

    public Color getBackground();

    public void setBackground(Color aValue);

    public static final String FOREGROUND_JSDOC = ""
            + "/**\n"
            + " * The foreground color of this component.\n"
            + " */";

    public Color getForeground();

    public void setForeground(Color aValue);

    public static final String VISIBLE_JSDOC = ""
            + "/**\n"
            + " * Determines whether this component should be visible when its parent is visible.\n"
            + " */";

    public boolean getVisible();

    public void setVisible(boolean aValue);

    public static final String FOCUSABLE_JSDOC = ""
            + "/**\n"
            + " * Determines whether this component may be focused.\n"
            + " */";

    public boolean getFocusable();

    public void setFocusable(boolean aValue);

    public static final String ENABLED_JSDOC = ""
            + "/**\n"
            + " * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.\n"
            + " */";

    public boolean getEnabled();

    public void setEnabled(boolean aValue);

    public static final String TOOLTIP_TEXT_JSDOC = ""
            + "/**\n"
            + " * The tooltip string that has been set with.\n"
            + " */";

    public String getToolTipText();

    public void setToolTipText(String aValue);

    public static final String OPAQUE_TEXT_JSDOC = ""
            + "/**\n"
            + " * True if this component is completely opaque.\n"
            + " */";

    public boolean getOpaque();

    public void setOpaque(boolean aValue);

    public static final String COMPONENT_POPUP_MENU_JSDOC = ""
            + "/**\n"
            + " * <code>PopupMenu</code> that assigned for this component.\n"
            + " */";

    public JPopupMenu getComponentPopupMenu();

    public void setComponentPopupMenu(JPopupMenu aMenu);

    public static final String FONT_JSDOC = ""
            + "/**\n"
            + " * The font of this component.\n"
            + " */";

    public Font getFont();

    public void setFont(Font aFont);

    public static final String CURSOR_JSDOC = ""
            + "/**\n"
            + " * The mouse <code>Cursor</code> over this component.\n"
            + " */";

    public Cursor getCursor();

    public void setCursor(Cursor aCursor);

    // Native API
    public static final String NATIVE_COMPONENT_JSDOC = ""
            + "/**\n"
            + " * Native API. Returns low level swing component. Applicable only in J2SE swing client.\n"
            + " */";

    public JComponent getComponent();

    public static final String NATIVE_ELEMENT_JSDOC = ""
            + "/**\n"
            + " * Native API. Returns low level html element. Applicable only in HTML5 client.\n"
            + " */";

    public Object getElement();
}
