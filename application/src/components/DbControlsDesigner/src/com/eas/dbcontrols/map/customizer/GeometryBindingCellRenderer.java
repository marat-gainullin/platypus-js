/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer;

import com.eas.dbcontrols.DbControlsDesignUtils;
import com.eas.dbcontrols.DesignIconCache;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

/**
 *
 * @author pk
 */
class GeometryBindingCellRenderer extends DefaultListCellRenderer
{
    private final Map<Class, String> TEXTS = new HashMap<>();

    public GeometryBindingCellRenderer()
    {
        final Class[] availBindings = GeometryBindingComboModel.AVAILABLE_BINDINGS;
        for (int i = 0; i < availBindings.length; i++)
            TEXTS.put(availBindings[i], DbControlsDesignUtils.getLocalizedString("GeometryBinding." + availBindings[i].getSimpleName())); //NOI18N
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        assert (c == this);
        setIcon(iconFor((Class) value));
        setText(textFor((Class) value));
        return this;
    }

    private Icon iconFor(Class binding)
    {
        return DesignIconCache.getIcon("16x16/" + binding.getSimpleName() + ".png");
    }

    private String textFor(Class binding)
    {
        final String translatedText = TEXTS.get(binding);
        return translatedText == null ? binding.getSimpleName() : translatedText;
    }
}
