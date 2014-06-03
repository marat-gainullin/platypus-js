/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.Form;
import com.eas.client.forms.PlatypusInternalFrame;
import com.eas.client.forms.api.Component;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class DesktopPane extends Component<JDesktopPane> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * Desktop pane panel component.\n"
            + " * This component can be used for creating a multi-document GUI or a virtual desktop.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public DesktopPane() {
        super();
        setDelegate(new JDesktopPane());
    }

    protected DesktopPane(JDesktopPane aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    private static final String MINIMIZE_ALL_JSDOC = ""
            + "/**\n"
            + "* Minimizes all frames on the pane.\n"
            + "*/";

    @ScriptFunction(jsDoc = MINIMIZE_ALL_JSDOC)
    public void minimizeAll() {
        for (JInternalFrame f : delegate.getAllFrames()) {
            delegate.getDesktopManager().iconifyFrame(f);
        }
    }

    private static final String RESTORE_ALL_JSDOC = ""
            + "/**\n"
            + "* Restores frames original state and location.\n"
            + "*/";

    @ScriptFunction(jsDoc = RESTORE_ALL_JSDOC)
    public void restoreAll() {
        for (JInternalFrame f : delegate.getAllFrames()) {
            delegate.getDesktopManager().deiconifyFrame(f);
        }
    }

    private static final String MAXIMIZE_ALL_JSDOC = ""
            + "/**\n"
            + "* Maximizes all frames on the pane.\n"
            + "*/";

    @ScriptFunction(jsDoc = MAXIMIZE_ALL_JSDOC)
    public void maximizeAll() {
        for (JInternalFrame f : delegate.getAllFrames()) {
            delegate.getDesktopManager().maximizeFrame(f);
        }
    }

    private static final String CLOSE_ALL_JSDOC = ""
            + "/**\n"
            + "* Closes all frames on the pane.\n"
            + "*/";

    @ScriptFunction(jsDoc = CLOSE_ALL_JSDOC)
    public void closeAll() {
        for (JInternalFrame f : delegate.getAllFrames()) {
            delegate.getDesktopManager().closeFrame(f);
        }
    }

    private static final String FORMS_JSDOC = ""
            + "/**\n"
            + "* An array of all frames on the pane.\n"
            + "*/";

    @ScriptFunction(jsDoc = FORMS_JSDOC)
    public Form[] getForms() {
        List<Form> forms = new ArrayList<>();
        for (JInternalFrame f : delegate.getAllFrames()) {
            if (f instanceof PlatypusInternalFrame) {
                PlatypusInternalFrame pif = (PlatypusInternalFrame) f;
                if (pif.getOwner() != null) {
                    forms.add(pif.getOwner());
                }
            }
        }
        return forms.toArray(new Form[]{});
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
