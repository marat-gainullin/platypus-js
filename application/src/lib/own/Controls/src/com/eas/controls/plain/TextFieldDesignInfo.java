/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.plain;

import com.eas.controls.ControlDesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;
import com.eas.gui.CascadedStyle;
import com.eas.store.Serial;
import java.awt.Color;

/**
 *
 * @author mg
 */
public class TextFieldDesignInfo extends ControlDesignInfo {

    protected int columns;
    protected int horizontalAlignment = 10;// javax.swing.SwingConstants.LEADING; 
    protected int scrollOffset;
    protected int caretPosition;
    protected boolean dragEnabled;
    protected boolean editable = true;
    protected int selectionEnd;
    protected int selectionStart;
    protected String text;
    protected String emptyText;
    protected Color caretColor;
    protected Color disabledTextColor;
    protected Color selectedTextColor;
    protected Color selectionColor;

    public TextFieldDesignInfo() {
        super();
    }

    @Serial
    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    @Serial
    public void setHorizontalAlignment(int aValue) {
        int oldValue = horizontalAlignment;
        horizontalAlignment = aValue;
        firePropertyChange("horizontalAlignment", oldValue, horizontalAlignment);
    }

    @Serial
    public int getScrollOffset() {
        return scrollOffset;
    }

    @Serial
    public void setScrollOffset(int aValue) {
        int oldValue = scrollOffset;
        scrollOffset = aValue;
        firePropertyChange("scrollOffset", oldValue, scrollOffset);
    }

    @Serial
    public int getColumns() {
        return columns;
    }

    @Serial
    public void setColumns(int aValue) {
        int oldValue = columns;
        columns = aValue;
        firePropertyChange("columns", oldValue, columns);
    }

    @Serial
    public int getCaretPosition() {
        return caretPosition;
    }

    @Serial
    public void setCaretPosition(int aValue) {
        int oldValue = caretPosition;
        caretPosition = aValue;
        firePropertyChange("caretPosition", oldValue, caretPosition);
    }

    @Serial
    public boolean isDragEnabled() {
        return dragEnabled;
    }

    @Serial
    public void setDragEnabled(boolean aValue) {
        boolean oldValue = dragEnabled;
        dragEnabled = aValue;
        firePropertyChange("dragEnabled", oldValue, dragEnabled);
    }

    @Serial
    public boolean isEditable() {
        return editable;
    }

    @Serial
    public void setEditable(boolean aValue) {
        boolean oldValue = editable;
        editable = aValue;
        firePropertyChange("editable", oldValue, editable);
    }

    @Serial
    public int getSelectionEnd() {
        return selectionEnd;
    }

    @Serial
    public void setSelectionEnd(int aValue) {
        int oldValue = selectionEnd;
        selectionEnd = aValue;
        firePropertyChange("selectionEnd", oldValue, selectionEnd);
    }

    @Serial
    public int getSelectionStart() {
        return selectionStart;
    }

    @Serial
    public void setSelectionStart(int aValue) {
        int oldValue = selectionStart;
        selectionStart = aValue;
        firePropertyChange("selectionStart", oldValue, selectionStart);
    }

    @Serial
    public String getText() {
        return text;
    }

    @Serial
    public void setText(String aValue) {
        String oldValue = text;
        text = aValue;
        firePropertyChange("text", oldValue, text);
    }

    @Serial
    public String getEmptyText() {
        return emptyText;
    }

    @Serial
    public void setEmptyText(String aValue) {
        String oldValue = emptyText;
        emptyText = aValue;
        firePropertyChange("emptyText", oldValue, emptyText);
    }

    public Color getCaretColor() {
        return caretColor;
    }

    public void setCaretColor(Color aValue) {
        Color oldValue = caretColor;
        caretColor = aValue;
        firePropertyChange("caretColor", oldValue, caretColor);
    }

    @Serial
    public String getTextCaretColor() {
        return caretColor != null ? CascadedStyle.encodeColor(caretColor) : null;
    }

    @Serial
    public void setTextCaretColor(String aValue) {
        Color oldValue = caretColor;
        if (aValue != null) {
            caretColor = Color.decode(aValue);
        } else {
            caretColor = null;
        }
        firePropertyChange("caretColor", oldValue, caretColor);
    }

    public Color getDisabledTextColor() {
        return disabledTextColor;
    }

    public void setDisabledTextColor(Color aValue) {
        Color oldValue = disabledTextColor;
        disabledTextColor = aValue;
        firePropertyChange("disabledTextColor", oldValue, disabledTextColor);
    }

    @Serial
    public String getTextDisabledColor() {
        return disabledTextColor != null ? CascadedStyle.encodeColor(disabledTextColor) : null;
    }

    @Serial
    public void setTextDisabledColor(String aValue) {
        Color oldValue = disabledTextColor;
        if (aValue != null) {
            disabledTextColor = Color.decode(aValue);
        } else {
            disabledTextColor = null;
        }
        firePropertyChange("disabledTextColor", oldValue, disabledTextColor);
    }

    public Color getSelectedTextColor() {
        return selectedTextColor;
    }

    public void setSelectedTextColor(Color aValue) {
        Color oldValue = selectedTextColor;
        selectedTextColor = aValue;
        firePropertyChange("selectedTextColor", oldValue, selectedTextColor);
    }

    @Serial
    public String getTextSelectedColor() {
        return selectedTextColor != null ? CascadedStyle.encodeColor(selectedTextColor) : null;
    }

    @Serial
    public void setTextSelectedColor(String aValue) {
        Color oldValue = selectedTextColor;
        if (aValue != null) {
            selectedTextColor = Color.decode(aValue);
        } else {
            selectedTextColor = null;
        }
        firePropertyChange("selectedTextColor", oldValue, selectedTextColor);
    }

    public Color getSelectionColor() {
        return selectionColor;
    }

    public void setSelectionColor(Color aValue) {
        Color oldValue = selectionColor;
        selectionColor = aValue;
        firePropertyChange("selectionColor", oldValue, selectionColor);
    }

    @Serial
    public String getTextSelectionColor() {
        return selectionColor != null ? CascadedStyle.encodeColor(selectionColor) : null;
    }

    @Serial
    public void setTextSelectionColor(String aValue) {
        Color oldValue = selectionColor;
        if (aValue != null) {
            selectionColor = Color.decode(aValue);
        } else {
            selectionColor = null;
        }
        firePropertyChange("selectionColor", oldValue, selectionColor);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        TextFieldDesignInfo other = (TextFieldDesignInfo) obj;
        if (this.horizontalAlignment != other.horizontalAlignment) {
            return false;
        }
        if (this.scrollOffset != other.scrollOffset) {
            return false;
        }
        if (this.columns != other.columns) {
            return false;
        }
        if (this.caretPosition != other.caretPosition) {
            return false;
        }
        if (this.dragEnabled != other.dragEnabled) {
            return false;
        }
        if (this.editable != other.editable) {
            return false;
        }
        if (this.selectionEnd != other.selectionEnd) {
            return false;
        }
        if (this.selectionStart != other.selectionStart) {
            return false;
        }
        if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
            return false;
        }
        if ((this.emptyText == null) ? (other.emptyText != null) : !this.emptyText.equals(other.emptyText)) {
            return false;
        }
        if (this.caretColor != other.caretColor && (this.caretColor == null || !this.caretColor.equals(other.caretColor))) {
            return false;
        }
        if (this.disabledTextColor != other.disabledTextColor && (this.disabledTextColor == null || !this.disabledTextColor.equals(other.disabledTextColor))) {
            return false;
        }
        if (this.selectedTextColor != other.selectedTextColor && (this.selectedTextColor == null || !this.selectedTextColor.equals(other.selectedTextColor))) {
            return false;
        }
        if (this.selectionColor != other.selectionColor && (this.selectionColor == null || !this.selectionColor.equals(other.selectionColor))) {
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
        if (aValue instanceof TextFieldDesignInfo) {
            TextFieldDesignInfo source = (TextFieldDesignInfo) aValue;
            horizontalAlignment = source.horizontalAlignment;
            scrollOffset = source.scrollOffset;
            columns = source.columns;
            caretPosition = source.caretPosition;
            dragEnabled = source.dragEnabled;
            editable = source.editable;
            selectionEnd = source.selectionEnd;
            selectionStart = source.selectionStart;

            text = source.text;
            emptyText = source.emptyText;
            caretColor = source.caretColor != null ? new Color(source.caretColor.getRed(), source.caretColor.getGreen(), source.caretColor.getBlue(), source.caretColor.getAlpha()) : null;
            disabledTextColor = source.disabledTextColor != null ? new Color(source.disabledTextColor.getRed(), source.disabledTextColor.getGreen(), source.disabledTextColor.getBlue(), source.disabledTextColor.getAlpha()) : null;
            selectedTextColor = source.selectedTextColor != null ? new Color(source.selectedTextColor.getRed(), source.selectedTextColor.getGreen(), source.selectedTextColor.getBlue(), source.selectedTextColor.getAlpha()) : null;
            selectionColor = source.selectionColor != null ? new Color(source.selectionColor.getRed(), source.selectionColor.getGreen(), source.selectionColor.getBlue(), source.selectionColor.getAlpha()) : null;

        }
    }
}
