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

    protected DesktopPane(JDesktopPane aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public DesktopPane() {
        super();
        setDelegate(new JDesktopPane());
    }
    
    @ScriptFunction(jsDocText="Minimizes all frames on the pane.")
    public void minimizeAll() {
        for (JInternalFrame f : delegate.getAllFrames()) {
            delegate.getDesktopManager().iconifyFrame(f);
        }
    }

    @ScriptFunction(jsDocText="Restores frames original state and location.")
    public void restoreAll() {
        for (JInternalFrame f : delegate.getAllFrames()) {
            delegate.getDesktopManager().deiconifyFrame(f);
        }
    }

    @ScriptFunction(jsDocText="Maximizes all frames on the pane.")
    public void maximizeAll() {
        for (JInternalFrame f : delegate.getAllFrames()) {
            delegate.getDesktopManager().maximizeFrame(f);
        }
    }

    @ScriptFunction(jsDocText="Closes all frames on the pane.")
    public void closeAll() {
        for (JInternalFrame f : delegate.getAllFrames()) {
            delegate.getDesktopManager().closeFrame(f);
        }
    }

    @ScriptFunction(jsDocText="Returns array of all frames on the pane.")
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
