/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls;

import com.eas.controls.containers.*;
import com.eas.controls.menus.*;
import com.eas.controls.plain.*;
import com.eas.store.ClassedSerialCollection;
import com.eas.store.PropertiesSimpleFactory;
import com.eas.store.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class FormDesignInfo extends ContainerDesignInfo implements PropertiesSimpleFactory {
    protected int defaultCloseOperation = 1;//WindowConstants.HIDE_ON_CLOSE; //This constant is inlined because we whant to be independent of Swing
    protected String iconImage;
    protected boolean alwaysOnTop;
    protected boolean resizable = true;
    protected boolean undecorated;
    protected float opacity = 1.0f;
    protected boolean locationByPlatform;
    protected String title;
    protected String jMenuBar;
    protected String windowOpened;
    protected String windowClosing;
    protected String windowClosed;
    protected String windowMinimized;
    protected String windowMaximized;
    protected String windowRestored;
    protected String windowActivated;
    protected String windowDeactivated;
    protected List<ControlDesignInfo> children = new ArrayList<>();
    protected List<DesignInfo> nonvisuals = new ArrayList<>();

    public FormDesignInfo() {
        super();
        visible = false;
    }

    @Serial
    @Override
    public boolean isVisible() {
        return false;
    }

    @Serial
    public String getIconImage() {
        return iconImage;
    }

    @Serial
    public void setIconImage(String aValue) {
        String oldValue = iconImage;
        iconImage = aValue;
        firePropertyChange("iconImage", oldValue, iconImage);
    }

    @Serial
    public int getDefaultCloseOperation() {
        return defaultCloseOperation;
    }

    @Serial
    public void setDefaultCloseOperation(int aValue) {
        int oldValue = defaultCloseOperation;
        defaultCloseOperation = aValue;
        firePropertyChange("defaultCloseOperation", oldValue, defaultCloseOperation);
    }

    @Serial
    public boolean isLocationByPlatform() {
        return locationByPlatform;
    }

    @Serial
    public void setLocationByPlatform(boolean aValue) {
        boolean oldValue = locationByPlatform;
        locationByPlatform = aValue;
        firePropertyChange("locationByPlatform", oldValue, locationByPlatform);
    }

    @Serial
    public boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }

    @Serial
    public void setAlwaysOnTop(boolean aValue) {
        boolean oldValue = alwaysOnTop;
        alwaysOnTop = aValue;
        firePropertyChange("alwaysOnTop", oldValue, alwaysOnTop);
    }

    @Serial
    public boolean isResizable() {
        return resizable;
    }

    @Serial
    public void setResizable(boolean aValue) {
        boolean oldValue = resizable;
        resizable = aValue;
        firePropertyChange("resizable", oldValue, resizable);
    }

    @Serial
    public boolean isUndecorated() {
        return undecorated;
    }

    @Serial
    public void setUndecorated(boolean aValue) {
        boolean oldValue = undecorated;
        undecorated = aValue;
        firePropertyChange("undecorated", oldValue, undecorated);
    }

    @Serial
    public float getOpacity() {
        return opacity;
    }

    @Serial
    public void setOpacity(float aOpacity) {
        float oldValue = opacity;
        opacity = aOpacity;
        firePropertyChange("opacity", oldValue, opacity);
    }

    @Serial
    public String getTitle() {
        return title;
    }

    @Serial
    public void setTitle(String aValue) {
        String oldValue = title;
        title = aValue;
        firePropertyChange("title", oldValue, title);
    }

    @Serial
    public String getJMenuBar() {
        return jMenuBar;
    }

    @Serial
    public void setJMenuBar(String aValue) {
        String oldValue = jMenuBar;
        jMenuBar = aValue;
        firePropertyChange("jMenuBar", oldValue, jMenuBar);
    }

    @Serial
    public String getWindowOpened() {
        return windowOpened;
    }

    @Serial
    public void setWindowOpened(String aValue) {
        String oldValue = windowOpened;
        windowOpened = aValue;
        firePropertyChange("windowOpened", oldValue, windowOpened);
    }

    @Serial
    public String getWindowClosing() {
        return windowClosing;
    }

    @Serial
    public void setWindowClosing(String aValue) {
        String oldValue = windowClosing;
        windowClosing = aValue;
        firePropertyChange("windowClosing", oldValue, windowClosing);
    }

    @Serial
    public String getWindowClosed() {
        return windowClosed;
    }

    @Serial
    public void setWindowClosed(String aValue) {
        String oldValue = windowClosed;
        windowClosed = aValue;
        firePropertyChange("windowClosed", oldValue, windowClosed);
    }

    @Serial
    public String getWindowMinimized() {
        return windowMinimized;
    }

    @Serial
    public void setWindowMinimized(String aValue) {
        String oldValue = windowMinimized;
        windowMinimized = aValue;
        firePropertyChange("windowMinimized", oldValue, windowMinimized);
    }

    @Serial
    public String getWindowMaximized() {
        return windowMaximized;
    }

    @Serial
    public void setWindowMaximized(String aValue) {
        String oldValue = windowMaximized;
        windowMaximized = aValue;
        firePropertyChange("windowMaximized", oldValue, windowMaximized);
    }

    @Serial
    public String getWindowRestored() {
        return windowRestored;
    }

    @Serial
    public void setWindowRestored(String aValue) {
        String oldValue = windowRestored;
        windowRestored = aValue;
        firePropertyChange("windowRestored", oldValue, windowRestored);
    }

    @Serial
    public String getWindowActivated() {
        return windowActivated;
    }

    @Serial
    public void setWindowActivated(String aValue) {
        String oldValue = windowActivated;
        windowActivated = aValue;
        firePropertyChange("windowActivated", oldValue, windowActivated);
    }

    @Serial
    public String getWindowDeactivated() {
        return windowDeactivated;
    }

    @Serial
    public void setWindowDeactivated(String aValue) {
        String oldValue = windowDeactivated;
        windowDeactivated = aValue;
        firePropertyChange("windowDeactivated", oldValue, windowDeactivated);
    }

    @ClassedSerialCollection(deserializeAs = ArrayList.class, elementTagName = "widget", elementClassHint = "type")
    public List<ControlDesignInfo> getChildren() {
        return children;
    }

    @ClassedSerialCollection(deserializeAs = ArrayList.class, elementTagName = "widget", elementClassHint = "type")
    public void setChildren(List<ControlDesignInfo> aValue) {
        List<ControlDesignInfo> oldValue = children;
        children = aValue;
        firePropertyChange("children", oldValue, children);
    }

    @ClassedSerialCollection(deserializeAs = ArrayList.class, elementTagName = "nonvisual", elementClassHint = "type")
    public List<DesignInfo> getNonvisuals() {
        return nonvisuals;
    }

    @ClassedSerialCollection(deserializeAs = ArrayList.class, elementTagName = "nonvisual", elementClassHint = "type")
    public void setNonvisuals(List<DesignInfo> aValue) {
        List<DesignInfo> oldValue = nonvisuals;
        nonvisuals = aValue;
        firePropertyChange("nonvisuals", oldValue, nonvisuals);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final FormDesignInfo other = (FormDesignInfo) obj;
        if (defaultCloseOperation != other.defaultCloseOperation) {
            return false;
        }
        if ((this.iconImage == null) ? (other.iconImage != null) : !this.iconImage.equals(other.iconImage)) {
            return false;
        }
        if (alwaysOnTop != other.alwaysOnTop) {
            return false;
        }
        if (resizable != other.resizable) {
            return false;
        }
        if (undecorated != other.undecorated) {
            return false;
        }
        if (Math.abs(opacity - other.opacity) > Float.MIN_NORMAL) {
            return false;
        }
        if (locationByPlatform != other.locationByPlatform) {
            return false;
        }
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if ((this.jMenuBar == null) ? (other.jMenuBar != null) : !this.jMenuBar.equals(other.jMenuBar)) {
            return false;
        }
        if ((this.windowOpened == null) ? (other.windowOpened != null) : !this.windowOpened.equals(other.windowOpened)) {
            return false;
        }
        if ((this.windowClosing == null) ? (other.windowClosing != null) : !this.windowClosing.equals(other.windowClosing)) {
            return false;
        }
        if ((this.windowClosed == null) ? (other.windowClosed != null) : !this.windowClosed.equals(other.windowClosed)) {
            return false;
        }
        if ((this.windowMinimized == null) ? (other.windowMinimized != null) : !this.windowMinimized.equals(other.windowMinimized)) {
            return false;
        }
        if ((this.windowMaximized == null) ? (other.windowMaximized != null) : !this.windowMaximized.equals(other.windowMaximized)) {
            return false;
        }
        if ((this.windowRestored == null) ? (other.windowRestored != null) : !this.windowRestored.equals(other.windowRestored)) {
            return false;
        }
        if ((this.windowActivated == null) ? (other.windowActivated != null) : !this.windowActivated.equals(other.windowActivated)) {
            return false;
        }
        if ((this.windowDeactivated == null) ? (other.windowDeactivated != null) : !this.windowDeactivated.equals(other.windowDeactivated)) {
            return false;
        }
        if (this.children != other.children && (this.children == null || !listsEquals(this.children, other.children))) {
            return false;
        }
        if (this.nonvisuals != other.nonvisuals && (this.nonvisuals == null || !listsEquals(this.nonvisuals, other.nonvisuals))) {
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
        if (aValue instanceof FormDesignInfo) {
            FormDesignInfo source = (FormDesignInfo) aValue;
            defaultCloseOperation = source.defaultCloseOperation;
            iconImage = source.iconImage != null ? new String(source.iconImage.toCharArray()) : null;
            alwaysOnTop = source.alwaysOnTop;
            resizable = source.resizable;
            undecorated = source.undecorated;
            opacity = source.opacity;
            locationByPlatform = source.locationByPlatform;
            title = source.title != null ? new String(source.title.toCharArray()) : null;
            jMenuBar = source.jMenuBar != null ? new String(source.jMenuBar.toCharArray()) : null;
            windowOpened = source.windowOpened != null ? new String(source.windowOpened.toCharArray()) : null;
            windowClosing = source.windowClosing != null ? new String(source.windowClosing.toCharArray()) : null;
            windowClosed = source.windowClosed != null ? new String(source.windowClosed.toCharArray()) : null;
            windowMinimized = source.windowMinimized != null ? new String(source.windowMinimized.toCharArray()) : null;
            windowMaximized = source.windowMaximized != null ? new String(source.windowMaximized.toCharArray()) : null;            
            windowRestored = source.windowRestored != null ? new String(source.windowRestored.toCharArray()) : null;
            windowActivated = source.windowActivated != null ? new String(source.windowActivated.toCharArray()) : null;
            windowDeactivated = source.windowDeactivated != null ? new String(source.windowDeactivated.toCharArray()) : null;

            if (source.children != null) {
                children = new ArrayList<>();
                for (int i = 0; i < source.children.size(); i++) {
                    children.add(i, (ControlDesignInfo) source.children.get(i).copy());
                }
            } else {
                children = null;
            }
            if (source.nonvisuals != null) {
                nonvisuals = new ArrayList<>();
                for (int i = 0; i < source.nonvisuals.size(); i++) {
                    nonvisuals.add(i, source.nonvisuals.get(i).copy());
                }
            } else {
                nonvisuals = null;
            }

        }
    }

    private boolean listsEquals(List<? extends DesignInfo> left, List<? extends DesignInfo> right) {
        assert left != null;
        if (right != null) {
            if (left.size() != right.size()) {
                return false;
            }
            for (int i = 0; i < left.size(); i++) {
                if (!left.get(i).isEqual(right.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object createPropertyObjectInstance(String aSimpleClassName) {
        if (LabelDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new LabelDesignInfo();
        } else if (ButtonDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new ButtonDesignInfo();
        } else if (ButtonGroupDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new ButtonGroupDesignInfo();
        } else if (CheckDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new CheckDesignInfo();
        } else if (TextPaneDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new TextPaneDesignInfo();
        } else if (EditorPaneDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new EditorPaneDesignInfo();
        } else if (FormattedFieldDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new FormattedFieldDesignInfo();
        } else if (PasswordFieldDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new PasswordFieldDesignInfo();
        } else if (ProgressBarDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new ProgressBarDesignInfo();
        } else if (RadioDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new RadioDesignInfo();
        } else if (SliderDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new SliderDesignInfo();
        } else if (TextFieldDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new TextFieldDesignInfo();
        } else if (ToggleButtonDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new ToggleButtonDesignInfo();
        } else if (DropDownButtonDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new DropDownButtonDesignInfo();
        } else if (DesktopDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new DesktopDesignInfo();
        } else if (LayersDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new LayersDesignInfo();
        } else if (PanelDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new PanelDesignInfo();
        } else if (ScrollDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new ScrollDesignInfo();
        } else if (SplitDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new SplitDesignInfo();
        } else if (TabsDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new TabsDesignInfo();
        } else if (ToolbarDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new ToolbarDesignInfo();
        } else if (MenuCheckItemDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new MenuCheckItemDesignInfo();
        } else if (MenuDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new MenuDesignInfo();
        } else if (MenuItemDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new MenuItemDesignInfo();
        } else if (MenuRadioItemDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new MenuRadioItemDesignInfo();
        } else if (MenuSeparatorDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new MenuSeparatorDesignInfo();
        } else if (MenubarDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new MenubarDesignInfo();
        } else if (PopupDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new PopupDesignInfo();
        } else {
            Object res = super.createPropertyObjectInstance(aSimpleClassName);
            if (res == null) {
                Logger.getLogger(ControlDesignInfo.class.getName()).severe(String.format("Neither plain controls nor layout or constraints found with such name:: %s", aSimpleClassName));
            }
            return res;
        }
    }
}
