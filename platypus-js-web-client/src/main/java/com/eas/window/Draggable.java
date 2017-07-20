package com.eas.window;

import com.eas.core.XElement;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.DOM;

/**
 *
 * @author mg
 */
public abstract class Draggable {

    private Element element = Document.get().createDivElement();
    protected int mouseScreenX;
    protected int mouseScreenY;

    public Draggable(String... aClasses) {
        super();
        for (String className : aClasses) {
            element.classList.add(className);
        }
        element.<XElement>cast().addEventListener(BrowserEvents.MOUSEDOWN, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                DOM.setCapture(element);
                evt.preventDefault();
                evt.stopPropagation();
                mouseScreenX = evt.getScreenX();
                mouseScreenY = evt.getScreenY();
                mousePressed();
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.MOUSEUP, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                evt.preventDefault();
                evt.stopPropagation();
                if (DOM.getCaptureElement() == element) {
                    DOM.releaseCapture(element);
                    int dx = evt.getScreenX() - mouseScreenX;
                    int dy = evt.getScreenY() - mouseScreenY;
                    changeTarget(dx, dy, evt);
                }
                mouseReleased();
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.MOUSEMOVE, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent evt) {
                if (DOM.getCaptureElement() == element) {
                    int dx = evt.getScreenX() - mouseScreenX;
                    int dy = evt.getScreenY() - mouseScreenY;
                    evt.preventDefault();
                    evt.stopPropagation();
                    if (dx != 0 || dy != 0) {
                        mouseDragged();
                    }
                    changeTarget(dx, dy, evt);
                }
            }

        });
    }

    public Element getElement() {
        return element;
    }

    protected abstract void mousePressed();

    protected abstract void mouseReleased();

    protected abstract void mouseDragged();

    protected abstract boolean changeTarget(int dx, int dy, NativeEvent aEvent);
    
    protected abstract void activate();
    
    protected abstract void deactivate();
}
