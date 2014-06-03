/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.HorizontalPosition;
import com.eas.client.forms.api.VerticalPosition;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import javax.swing.Icon;
import javax.swing.JLabel;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class Label extends Component<JLabel> {

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* Label component.\n"
            + "* @param text the initial text for the component (optional)\n"
            + "* @param icon the icon for the component (optional)\n"
            + "* @param iconTextGap the text gap (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text", "icon", "iconTextGap"})
    public Label(String aText, Icon aIcon, int aIconTextGap) {
        super();
        setDelegate(new JLabel(aText, aIcon, JLabel.LEFT));
        delegate.setIconTextGap(aIconTextGap);
    }

    public Label(String aText, Icon aIcon) {
        this(aText, aIcon, 4);
    }

    public Label(String aText) {
        this(aText, null, 4);
    }

    public Label() {
        this(null, null, 4);
    }

    protected Label(JLabel aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The text string that the label displays.\n"
            + " */")
    public String getText() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The graphic image (glyph, icon) that the label displays.\n"
            + " */")
    public Icon getIcon() {
        return delegate.getIcon();
    }

    @ScriptFunction
    public void setIcon(Icon aValue) {
        delegate.setIcon(aValue);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The amount of space between the text and the icon displayed in this label.\n"
            + " */")
    public int getIconTextGap() {
        return delegate.getIconTextGap();
    }

    @ScriptFunction
    public void setIconTextGap(int aValue) {
        delegate.setIconTextGap(aValue);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Horizontal position of the text relative to the icon.\n"
            + " */")
    public int getHorizontalTextPosition() {
        switch (delegate.getHorizontalTextPosition()) {
            case JLabel.LEFT:
                return HorizontalPosition.LEFT;
            case JLabel.CENTER:
                return HorizontalPosition.CENTER;
            case JLabel.RIGHT:
                return HorizontalPosition.RIGHT;
            default:
                return HorizontalPosition.LEFT;
        }
    }

    @ScriptFunction
    public void setHorizontalTextPosition(int aValue) {
        switch (aValue) {
            case HorizontalPosition.LEFT:
                delegate.setHorizontalTextPosition(JLabel.LEFT);
                break;
            case HorizontalPosition.CENTER:
                delegate.setHorizontalTextPosition(JLabel.CENTER);
                break;
            case HorizontalPosition.RIGHT:
                delegate.setHorizontalTextPosition(JLabel.RIGHT);
                break;
            default:
                delegate.setHorizontalTextPosition(JLabel.LEFT);
                break;
        }
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Horizontal position of the text with the icon relative to the component's size.\n"
            + " */")
    public int getHorizontalAlignment() {
        switch (delegate.getHorizontalAlignment()) {
            case JLabel.LEFT:
                return HorizontalPosition.LEFT;
            case JLabel.CENTER:
                return HorizontalPosition.CENTER;
            case JLabel.RIGHT:
                return HorizontalPosition.RIGHT;
            default:
                return HorizontalPosition.LEFT;
        }
    }

    @ScriptFunction
    public void setHorizontalAlignment(int aValue) {
        switch (aValue) {
            case HorizontalPosition.LEFT:
                delegate.setHorizontalAlignment(JLabel.LEFT);
                break;
            case HorizontalPosition.CENTER:
                delegate.setHorizontalAlignment(JLabel.CENTER);
                break;
            case HorizontalPosition.RIGHT:
                delegate.setHorizontalAlignment(JLabel.RIGHT);
                break;
            default:
                delegate.setHorizontalAlignment(JLabel.LEFT);
                break;
        }
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Vertical position of the text relative to the icon.\n"
            + " */")
    public int getVerticalTextPosition() {
        switch (delegate.getVerticalTextPosition()) {
            case JLabel.TOP:
                return VerticalPosition.TOP;
            case JLabel.CENTER:
                return VerticalPosition.CENTER;
            case JLabel.BOTTOM:
                return VerticalPosition.BOTTOM;
            default:
                return VerticalPosition.CENTER;
        }
    }

    @ScriptFunction
    public void setVerticalTextPosition(int aValue) {
        switch (aValue) {
            case VerticalPosition.TOP:
                delegate.setVerticalTextPosition(JLabel.TOP);
                break;
            case VerticalPosition.CENTER:
                delegate.setVerticalTextPosition(JLabel.CENTER);
                break;
            case VerticalPosition.BOTTOM:
                delegate.setVerticalTextPosition(JLabel.BOTTOM);
                break;
            default:
                delegate.setVerticalTextPosition(JLabel.CENTER);
                break;
        }
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Vertical position of the text with the icon relative to the component's size.\n"
            + " */")
    public int getVerticalAlignment() {
        switch (delegate.getVerticalAlignment()) {
            case JLabel.TOP:
                return VerticalPosition.TOP;
            case JLabel.CENTER:
                return VerticalPosition.CENTER;
            case JLabel.BOTTOM:
                return VerticalPosition.BOTTOM;
            default:
                return VerticalPosition.CENTER;
        }
    }

    @ScriptFunction
    public void setVerticalAlignment(int aValue) {
        switch (aValue) {
            case VerticalPosition.TOP:
                delegate.setVerticalAlignment(JLabel.TOP);
                break;
            case VerticalPosition.CENTER:
                delegate.setVerticalAlignment(JLabel.CENTER);
                break;
            case VerticalPosition.BOTTOM:
                delegate.setVerticalAlignment(JLabel.BOTTOM);
                break;
            default:
                delegate.setVerticalAlignment(JLabel.CENTER);
                break;
        }
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
