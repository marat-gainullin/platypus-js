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
    protected boolean minimizable = true;
    protected boolean maximizable = true;
    protected boolean undecorated;
    protected float opacity = 1.0f;
    protected boolean locationByPlatform;
    protected String title;
    protected String jMenuBar;
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
    public boolean isMaximizable() {
        return maximizable;
    }

    @Serial
    public void setMaximizable(boolean aValue) {
        boolean oldValue = maximizable;
        maximizable = aValue;
        firePropertyChange("maximizable", oldValue, maximizable);
    }

    @Serial
    public boolean isMinimizable() {
        return minimizable;
    }

    @Serial
    public void setMinimizable(boolean aValue) {
        boolean oldValue = minimizable;
        minimizable = aValue;
        firePropertyChange("minimizable", oldValue, minimizable);
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
