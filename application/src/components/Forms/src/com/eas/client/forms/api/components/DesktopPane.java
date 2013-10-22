/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.FormRunner;
import com.eas.client.forms.PlatypusInternalFrame;
import com.eas.client.forms.api.Component;
import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 *
 * @author mg
 */
public class DesktopPane extends Component<JDesktopPane> {

private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* Desktop pane panel component.\n"
            + "* This component can be used for creating a multi-document GUI or a virtual desktop.\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public DesktopPane() {
        super();
        setDelegate(new JDesktopPane());
    }
    
    protected DesktopPane(JDesktopPane aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    @ScriptFunction(jsDoc="Minimizes all frames on the pane.")
    public void minimizeAll() {
        for (JInternalFrame f : delegate.getAllFrames()) {
            delegate.getDesktopManager().iconifyFrame(f);
        }
    }

    @ScriptFunction(jsDoc="Restores frames original state and location.")
    public void restoreAll() {
        for (JInternalFrame f : delegate.getAllFrames()) {
            delegate.getDesktopManager().deiconifyFrame(f);
        }
    }

    @ScriptFunction(jsDoc="Maximizes all frames on the pane.")
    public void maximizeAll() {
        for (JInternalFrame f : delegate.getAllFrames()) {
            delegate.getDesktopManager().maximizeFrame(f);
        }
    }

    @ScriptFunction(jsDoc="Closes all frames on the pane.")
    public void closeAll() {
        for (JInternalFrame f : delegate.getAllFrames()) {
            delegate.getDesktopManager().closeFrame(f);
        }
    }

    @ScriptFunction(jsDoc="Returns array of all frames on the pane.")
    public FormRunner[] getForms() {
        List<FormRunner> forms = new ArrayList<>();
        for (JInternalFrame f : delegate.getAllFrames()) {
            if (f instanceof PlatypusInternalFrame) {
                PlatypusInternalFrame pif = (PlatypusInternalFrame) f;
                if (pif.getOwner() != null) {
                    forms.add(pif.getOwner());
                }
            }
        }
        return forms.toArray(new FormRunner[]{});
    }
}
