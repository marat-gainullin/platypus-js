/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.open.support.script;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.GridView;
import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Marat
 */
public class VirtualPropsJavaObject extends NativeJavaObject {

    protected HashMap<String, VirtualProp> virtualProps = new HashMap<String, VirtualProp>();

    public VirtualPropsJavaObject(Scriptable scope, Object value, Scriptable receptor) {
        super(scope, value, null);
        initVirtualProps(receptor);
    }

    public HashMap<String, VirtualProp> getVirtualProps() {
        return virtualProps;
    }

    @Override
    public Object get(String name, Scriptable start) {
        Object prop = super.get(name, start);
        if (prop == null || prop == Scriptable.NOT_FOUND) {
            String lname = new String();
            lname = lname.concat(name);
            lname = lname.toLowerCase();
            if (virtualProps.containsKey(lname)) {
                VirtualProp vp = virtualProps.get(lname);
                if (vp != null) {
                    prop = vp.getScriptable(this);
                }
            }
        }
        return prop;
    }

    @Override
    public boolean has(String name, Scriptable start) {
        boolean lhas = super.has(name, start);
        if (!lhas) {
            String lname = new String();
            lname = lname.concat(name);
            lhas = (virtualProps.containsKey(lname.toLowerCase()));
        }
        return lhas;
    }

    protected void initVirtualProps(Scriptable receptor) {
        if (javaObject instanceof Container) {
            registerCompsByNames((Container) javaObject, receptor);
        }
    }

    private boolean isFrame(Component aItem) {
        if (aItem != null && aItem instanceof GridView) {
            GridView gv = (GridView) aItem;
            if (gv.getParentForm() != null) {
                return gv.getParentForm().isLinked();
            }
        }
        return false;
    }

    private void registerCompByName(Component lItem, Scriptable receptor) {
        if (lItem.getName() != null && !lItem.getName().equals("")) {
            String name = lItem.getName().toLowerCase();
            if (!virtualProps.containsKey(name)) {
                VirtualProp value = null;
                if (isFrame(lItem)) {
                    value = new VirtualProp(lItem, new VirtualPropsJavaObject(this, lItem, null));
                    virtualProps.put(name, value);
                } else {
                    value = new VirtualProp(lItem, null);
                    virtualProps.put(name, value);
                }
                if (receptor != null) {
                    receptor.put(lItem.getName(), receptor, value.getScriptable(this));
                }
            }
        }
    }

    private void registerCompsByNames(Container root, Scriptable receptor) {
        if (root != null) {
            if (root instanceof JMenu) {
                JMenu menu = (JMenu) root;
                MenuElement[] mElements = menu.getSubElements();
                for (int i = 0; i < mElements.length; i++) {
                    registerCompByName(mElements[i].getComponent(), receptor);
                    MenuElement[] mSubElements = mElements[i].getSubElements();
                    if(mSubElements != null && mSubElements.length > 0)
                    {
                        assert mElements[i].getComponent() instanceof Container;
                        registerCompsByNames((Container)mElements[i].getComponent(), receptor);
                    }
                }
            } else {
                Container lcontainer = root;
                for (int i = 0; i < lcontainer.getComponentCount(); i++) {
                    Component lItem = lcontainer.getComponent(i);
                    registerCompByName(lItem, receptor);
                    if(lItem instanceof JETABean &&
                       ((JETABean)lItem).getDelegate() instanceof JPopupMenu)
                        registerCompsByNames((Container) ((JETABean)lItem).getDelegate(), receptor);
                    else if (lItem instanceof Container && !isFrame(lItem)) {
                        registerCompsByNames((Container) lItem, receptor);
                    }

                }
            }
        }
    }
}
