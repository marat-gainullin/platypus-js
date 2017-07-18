package com.eas.ui;

import com.eas.core.Utils;
import com.eas.core.Logger;
import com.eas.core.XElement;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;

public class JsUi {

    public static void jsSelectFile(final JavaScriptObject aCallback, final String aFileTypes) {
        if (aCallback != null) {
            selectFile(new Callback<JavaScriptObject, String>() {

                @Override
                public void onSuccess(JavaScriptObject result) {
                    try {
                        Utils.executeScriptEventVoid(aCallback, aCallback, result);
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }

                @Override
                public void onFailure(String reason) {
                }

            }, aFileTypes);
        }
    }

    public static void selectFile(final Callback<JavaScriptObject, String> aCallback, String aFileTypes) {
        final FileUpload fu = new FileUpload();
        fu.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        fu.setWidth("10px");
        fu.setHeight("10px");
        fu.getElement().getStyle().setLeft(-100, Style.Unit.PX);
        fu.getElement().getStyle().setTop(-100, Style.Unit.PX);
        fu.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                Utils.JsObject jsFu = fu.getElement().cast();
                JavaScriptObject oFiles = jsFu.getJs("files");
                if (oFiles != null) {
                    JsArray<JavaScriptObject> jsFiles = oFiles.cast();
                    for (int i = 0; i < jsFiles.length(); i++) {
                        try {
                            aCallback.onSuccess(jsFiles.get(i));
                        } catch (Exception ex) {
                            Logger.severe(ex);
                        }
                    }
                }
                fu.removeFromParent();
            }
        });
        RootPanel.get().add(fu, -100, -100);
        fu.click();
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                fu.removeFromParent();
                return false;
            }
        }, 1000 * 60 * 1);// 1 min
    }

    public static void jsSelectColor(String aOldValue, final JavaScriptObject aCallback) {
        if (aCallback != null) {
            selectColor(aOldValue, new Callback<String, String>() {

                @Override
                public void onSuccess(String result) {
                    try {
                        Utils.executeScriptEventVoid(aCallback, aCallback, result);
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }

                @Override
                public void onFailure(String reason) {
                }

            });
        }
    }

    public static void selectColor(String aOldValue, final Callback<String, String> aCallback) {
        final TextBox tb = new TextBox();
        tb.getElement().setAttribute("type", "color");
        tb.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        tb.setWidth("10px");
        tb.setHeight("10px");
        tb.setValue(aOldValue);
        tb.getElement().getStyle().setLeft(-100, Style.Unit.PX);
        tb.getElement().getStyle().setTop(-100, Style.Unit.PX);

        tb.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                try {
                    aCallback.onSuccess(tb.getValue());
                } finally {
                    tb.removeFromParent();
                }
            }

        });
        RootPanel.get().add(tb, -100, -100);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                tb.setFocus(true);
                tb.getElement().<XElement>cast().click();
                Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
                    @Override
                    public boolean execute() {
                        tb.removeFromParent();
                        return false;
                    }
                }, 1000 * 60 * 1);// 1 min
            }
        });
    }

    public static Element findLayoutElementByBundleName(Element aElement, String aBundleName) {
        if (aElement.getTagName().equals("layout")) {
            return aElement;// the high level code had to do everything in the right way
        } else {
            Node child = aElement.getFirstChild();
            while (child != null) {
                if (child instanceof Element) {
                    Element el = (Element) child;
                    if (el.hasAttribute("bundle-name")) {
                        String bundleName = el.getAttribute("bundle-name");
                        if (bundleName.equals(aBundleName)) {
                            return el;
                        }
                    }
                }
                child = child.getNextSibling();
            }
        }
        return null;
    }
}
