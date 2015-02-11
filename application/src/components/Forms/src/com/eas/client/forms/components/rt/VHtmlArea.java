/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JEditorPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Марат
 */
public class VHtmlArea extends JEditorPane implements HasValue<String>, HasEmptyText, HasEditable {
    
    private String value;
    private boolean modified;

    public VHtmlArea(String aText) {
        super();
        super.setContentType("text/html");
        super.setText(aText != null ? aText : "");
        super.addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusLost(FocusEvent e) {
                if (modified) {
                    setValue(superGetText());
                }
            }
            
        });
        getDocument().addDocumentListener(new DocumentListener() {
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                modified = true;
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                modified = true;
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                modified = true;
            }
        });
    }
    
    private String superGetText() {
        return super.getText();
    }
    
    @Override
    public String getValue() {
        return value;
    }
    
    @Override
    public void setValue(String aValue) {
        if (value == null ? aValue != null : !value.equals(aValue)) {
            String oldValue = value;
            value = aValue;
            super.setText(value != null ? value : "");
            modified = false;
            firePropertyChange(VALUE_PROP_NAME, oldValue, value);
        }
    }
    
    @Override
    public void addValueChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(VALUE_PROP_NAME, listener);
    }
    
    @Override
    public void removeValueChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(VALUE_PROP_NAME, listener);
    }
    
    @Override
    public boolean getEditable() {
        return super.isEditable();
    }
    
    protected String emptyText;
    
    @Override
    public String getEmptyText() {
        return emptyText;
    }
    
    @Override
    public void setEmptyText(String aValue) {
        emptyText = aValue;
    }
    
    @Override
    public String getText() {
        return value != null ? value : "";
    }
    
    @Override
    public void setText(String aValue) {
        setValue(aValue);
    }
    
}
