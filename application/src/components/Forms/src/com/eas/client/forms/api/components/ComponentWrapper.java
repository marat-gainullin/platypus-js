/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.gui.JDropDownButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;

/**
 *
 * @author mg
 */
public class ComponentWrapper {
    
    public static CheckBox wrap(JCheckBox aDelegate)
    {
        return new CheckBox(aDelegate);
    }
    
    public static Button wrap(JButton aDelegate)
    {
        return new Button(aDelegate);
    }
    
    public static DropDownButton wrap(JDropDownButton aDelegate)
    {
        return new DropDownButton(aDelegate);
    }
    
    public static ToggleButton wrap(JToggleButton aDelegate)
    {
        return new ToggleButton(aDelegate);
    }
    
    public static Label wrap(JLabel aDelegate)
    {
        return new Label(aDelegate);
    }
    
    public static PasswordField wrap(JPasswordField aDelegate)
    {
        return new PasswordField(aDelegate);
    }
    
    public static FormattedField wrap(JFormattedTextField aDelegate)
    {
        return new FormattedField(aDelegate);
    }
    
    public static ProgressBar wrap(JProgressBar aDelegate)
    {
        return new ProgressBar(aDelegate);
    }
    
    public static RadioButton wrap(JRadioButton aDelegate)
    {
        return new RadioButton(aDelegate);
    }
    
    public static Slider wrap(JSlider aDelegate)
    {
        return new Slider(aDelegate);
    }
    
    public static TextArea wrap(JTextPane aDelegate)
    {
        return new TextArea(aDelegate);
    }
    
    public static HtmlArea wrap(JEditorPane aDelegate)
    {
        return new HtmlArea(aDelegate);
    }
    
    public static TextField wrap(JTextField aDelegate)
    {
        return new TextField(aDelegate);
    }
    
    public static DesktopPane wrap(JDesktopPane aDelegate)
    {
        return new DesktopPane(aDelegate);
    }
    
}
