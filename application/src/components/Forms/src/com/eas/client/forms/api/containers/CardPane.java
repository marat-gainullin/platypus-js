/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.events.ChangeEvent;
import com.eas.controls.wrappers.PlatypusCardLayout;
import com.eas.script.EventMethod;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class CardPane extends Container<JPanel> {

    protected JSObject onItemSelected;
    protected ItemListener cardsChangeListener = (ItemEvent e) -> {
        try {
            onItemSelected.call(getPublished(), new Object[]{new ChangeEvent(new javax.swing.event.ChangeEvent(CardPane.this)).getPublished()});
        } catch (Exception ex) {
            Logger.getLogger(CardPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    };

    public CardPane() {
        this(0, 0);
    }

    public CardPane(int hgap) {
        this(hgap, 0);
    }

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* A container with Card Layout. It treats each component in the container as a card. Only one card is visible at a time, and the container acts as a stack of cards.\n"
            + "* @param hgap the horizontal gap (optional).\n"
            + "* @param vgap the vertical gap (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"hgap", "vgap"})
    public CardPane(int hgap, int vgap) {
        super();
        setDelegate(new JPanel(new PlatypusCardLayout(hgap, vgap)));
    }

    protected CardPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof PlatypusCardLayout;
        setDelegate(aDelegate);
    }

    @Override
    protected void setDelegate(JPanel aDelegate) {
        if (delegate != null) {
            PlatypusCardLayout layout = (PlatypusCardLayout) aDelegate.getLayout();
            layout.removeChangeListener(cardsChangeListener);
        }
        super.setDelegate(aDelegate);
        if (delegate != null) {
            PlatypusCardLayout layout = (PlatypusCardLayout) aDelegate.getLayout();
            layout.addChangeListener(cardsChangeListener);
        }
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Event that is fired when one of the components is selected in this card pane.\n"
            + " */")
    @EventMethod(eventClass = ChangeEvent.class)
    public JSObject getOnItemSelected() {
        return onItemSelected;
    }

    @ScriptFunction
    public void setOnItemSelected(JSObject aValue) {
        if (onItemSelected != aValue) {
            onItemSelected = aValue;
        }
    }

    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Appends the component to this container with the specified name.\n"
            + "* @param component the component to add.\n"
            + "* @param cardName the name of the card.\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params={"component", "cardName"})
    public void add(Component<?> aComp, String aCardName) {
        if (aComp != null) {
            delegate.add(unwrap(aComp), aCardName);
            delegate.revalidate();
            delegate.repaint();
        }
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Gets child component, associated with the specified card.\n"
            + " * @param cardName Name of the card.\n"
            + " * @return the child component.\n"
            + "*/", params = {"cardName"})
    public Component<?> child(String aCardName) {
        PlatypusCardLayout layout = (PlatypusCardLayout) delegate.getLayout();
        return getComponentWrapper(layout.getComponent(aCardName));
    }

    private static final String SHOW_JSDOC = ""
            + "/**\n"
            + "* Flips to the component that was added to this layout with the specified name.\n"
            + "* @param name the card name\n"
            + "*/";

    @ScriptFunction(jsDoc = SHOW_JSDOC, params = {"name"})
    public void show(String aCardName) {
        PlatypusCardLayout layout = (PlatypusCardLayout) delegate.getLayout();
        layout.show(delegate, aCardName);
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject)publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
