/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Orientation;
import com.eas.script.ScriptFunction;
import javax.swing.JSlider;

/**
 *
 * @author mg
 */
public class Slider extends Component<JSlider> {

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* Slider component.\n"
            + "* @param min Minimum value (optional)\n"
            + "* @param max Maximum value (optional)\n"
            + "* @param value Initial value (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"min", "max", "value"}) 
    public Slider(int aOrientation, int min, int max, int value) {
        super();
        int orientation = JSlider.HORIZONTAL;
        if (aOrientation == Orientation.HORIZONTAL) {
            orientation = JSlider.HORIZONTAL;
        } else if (aOrientation == Orientation.VERTICAL) {
            orientation = JSlider.VERTICAL;
        }
        setDelegate(new JSlider(orientation, min, max, value));
    }

    public Slider(int aOrientation) {
        this(aOrientation, 0, 0, 0);
    }

    public Slider(int min, int max, int value) {
        this(Orientation.HORIZONTAL, min, max, value);
    }

    protected Slider(JSlider aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    @ScriptFunction(jsDoc = "This slider's vertical or horizontal orientation: Orientation.VERTICAL or Orientation.HORIZONTAL")
    public int getOrientation() {
        if (delegate.getOrientation() == JSlider.HORIZONTAL) {
            return Orientation.HORIZONTAL;
        } else if (delegate.getOrientation() == JSlider.VERTICAL) {
            return Orientation.VERTICAL;
        } else {
            return Orientation.HORIZONTAL;
        }
    }

    @ScriptFunction
    public void setOrientation(int aOrientation) {
        int orientation = JSlider.HORIZONTAL;
        if (aOrientation == Orientation.HORIZONTAL) {
            orientation = JSlider.HORIZONTAL;
        } else if (aOrientation == Orientation.VERTICAL) {
            orientation = JSlider.VERTICAL;
        }
        delegate.setOrientation(orientation);
    }

    @ScriptFunction(jsDoc = "The minimum value supported by the slider.")
    public int getMinimum() {
        return delegate.getMinimum();
    }

    @ScriptFunction
    public void setMinimum(int aValue) {
        delegate.setMinimum(aValue);
    }

    @ScriptFunction(jsDoc = "The maximum value supported by the slider.")
    public int getMaximum() {
        return delegate.getMaximum();
    }

    @ScriptFunction
    public void setMaximum(int aValue) {
        delegate.setMaximum(aValue);
    }
    
    @ScriptFunction(jsDoc = "The slider's current value")
    public int getValue() {
        return delegate.getValue();
    }
    
    @ScriptFunction
    public void setValue(int aValue) {
        delegate.setValue(aValue);
    }
}
