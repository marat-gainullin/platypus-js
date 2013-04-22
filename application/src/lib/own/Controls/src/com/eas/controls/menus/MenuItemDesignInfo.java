/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.menus;

import com.eas.store.Serial;
import com.eas.controls.DesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.SerialKeyStroke;
import com.eas.controls.plain.ButtonDesignInfo;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 *
 * @author mg
 */
public class MenuItemDesignInfo extends ButtonDesignInfo {

    protected KeyStroke accelerator;

    public MenuItemDesignInfo()
    {
        super();
        borderPainted = false;
        focusPainted = false;
        opaque = false;
        horizontalAlignment = SwingConstants.LEADING;
    }

    public KeyStroke getAccelerator() {
        return accelerator;
    }

    public void setAccelerator(KeyStroke aValue) {
        KeyStroke oldValue = accelerator;
        accelerator = aValue;
        firePropertyChange("accelerator", oldValue, accelerator);
    }

    @Serial
    public SerialKeyStroke getEasAccelerator() {
        return accelerator != null ? new SerialKeyStroke(accelerator) : null;
    }

    @Serial
    public void setEasAccelerator(SerialKeyStroke aValue) {
        KeyStroke oldValue = accelerator;
        if (aValue != null) {
            accelerator = aValue.getDelegate();
        }else
            accelerator = null;
        firePropertyChange("accelerator", oldValue, accelerator);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        MenuItemDesignInfo other = (MenuItemDesignInfo) obj;
        if (this.accelerator != other.accelerator && (this.accelerator == null || !this.accelerator.equals(other.accelerator))) {
            return false;
        }
        return true;
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public void assign(DesignInfo aValue) {
        super.assign(aValue);
        if (aValue instanceof MenuItemDesignInfo) {
            MenuItemDesignInfo source = (MenuItemDesignInfo) aValue;
            if (source.accelerator != null) {
                if (source.accelerator.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
                    accelerator = KeyStroke.getKeyStroke(source.accelerator.getKeyCode(), source.accelerator.getModifiers(), source.accelerator.isOnKeyRelease());
                } else {
                    accelerator = KeyStroke.getKeyStroke(Character.valueOf(source.accelerator.getKeyChar()), source.accelerator.getModifiers());
                }
            } else {
                accelerator = null;
            }
        }
    }
}
