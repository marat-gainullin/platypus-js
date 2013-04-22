/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query;

import javax.swing.JEditorPane;
import org.openide.nodes.Node;

/**
 * Simple cookie defining getting current opened editor pane, same as
 * EditorCookie.getOpenedPane()[0]
 *
 * @author vv
 */
public interface OpenedPaneEditorCookie extends Node.Cookie {

    /**
     * Get first editor pane opened on this object. The item in should represent
     * the component that is currently selected or that was most recently
     * selected. (Typically, multiple panes will only be open as a result of
     * cloning the editor component.)
     *
     * <p>The resulting panes are useful for a range of tasks; most commonly,
     * getting the current cursor position or text selection, including the
     * <code>Caret</code> object. <p>This method may also be used to test
     * whether an object is already open in an editor, without actually opening
     * it.
     *
     * @return an editor pane, or
     * <code>null</code> if no pane is open from this file.
     */
    public JEditorPane getOpenedPane();
}
