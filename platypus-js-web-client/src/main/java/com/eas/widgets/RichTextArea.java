package com.eas.widgets;

import com.eas.ui.ButtonGroup;
import com.eas.ui.CommonResources;
import com.eas.ui.HasEmptyText;
import com.eas.ui.HasScroll;
import com.eas.ui.HorizontalScrollFiller;
import com.eas.ui.VerticalScrollFiller;
import com.eas.ui.ValueWidget;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.HasFocusHandlers;
import com.eas.ui.events.HasBlurHandlers;
import com.eas.ui.Focusable;
import com.eas.ui.JsUi;
import com.eas.core.Logger;
import com.eas.core.XElement;
import com.eas.core.HasPublished;
import com.eas.ui.events.BlurEvent;
import com.eas.ui.FocusEvent;
import com.eas.ui.Widget;
import com.eas.ui.events.BlurHandler;
import com.eas.ui.events.FocusHandler;
import com.eas.ui.events.HasKeyDownHandlers;
import com.eas.ui.events.HasKeyPressHandlers;
import com.eas.ui.events.HasKeyUpHandlers;
import com.eas.ui.events.KeyDownEvent;
import com.eas.ui.events.KeyDownHandler;
import com.eas.ui.events.KeyPressEvent;
import com.eas.ui.events.KeyPressHandler;
import com.eas.ui.events.KeyUpEvent;
import com.eas.ui.events.KeyUpHandler;
import com.eas.ui.events.ValueChangeHandler;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.widgets.WidgetsUtils;
import com.eas.widgets.containers.Flow;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.HashSet;
import java.util.Set;

public class RichTextArea extends ValueWidget implements HasScroll, HorizontalScrollFiller, VerticalScrollFiller, HasEmptyText,
        Focusable, HasFocusHandlers, HasBlurHandlers,
        HasKeyDownHandlers, HasKeyUpHandlers, HasKeyPressHandlers {

    protected String emptyText;

    protected Element textContainer = Document.get().createDivElement();
    protected Flow toolbar = new Flow();

    // TODO: publidh API to manipulate controls from client code
    protected ImageButton btnBold = new ImageButton();
    protected ImageButton btnItalic = new ImageButton();
    protected ImageButton btnUnderline = new ImageButton();
    protected ButtonGroup groupSubSuperScript = new ButtonGroup();
    protected ImageToggleButton tglSubScript = new ImageToggleButton();
    protected ImageToggleButton tglSuperScript = new ImageToggleButton();
    protected ButtonGroup groupAlingment = new ButtonGroup();
    protected ImageToggleButton tglAlignLeft = new ImageToggleButton();
    protected ImageToggleButton tglAlignCenter = new ImageToggleButton();
    protected ImageToggleButton tglAlignFull = new ImageToggleButton();
    protected ImageToggleButton tglAlignRight = new ImageToggleButton();
    protected ButtonGroup groupCcp = new ButtonGroup();
    protected ImageButton btnCopy = new ImageButton();
    protected ImageButton btnCut = new ImageButton();
    protected ImageButton btnPaste = new ImageButton();
    protected ButtonGroup groupUndoRedo = new ButtonGroup();
    protected ImageButton btnUndo = new ImageButton();
    protected ImageButton btnRedo = new ImageButton();
    protected ImageButton btnBackground = new ImageButton();
    protected ImageButton btnForeground = new ImageButton();
    protected ImageButton btnStrikeTrough = new ImageButton();
    protected ImageButton btnIdentRight = new ImageButton();
    protected ImageButton btnIdentLeft = new ImageButton();
    protected ImageButton btnHorizontalRule = new ImageButton();
    protected ImageButton btnOrderedList = new ImageButton();
    protected ImageButton btnUnOrderedList = new ImageButton();
    protected ImageButton btnImage = new ImageButton();
    protected ImageButton btnUploadImage = new ImageButton();
    protected ImageButton btnCreateLink = new ImageButton();
    protected ImageButton btnRemoveLink = new ImageButton();
    protected DropDownList fonts = createFontList();
    protected DropDownList fontSizes = createFontSizes();
    protected ImageButton btnClearFormatting = new ImageButton();

    public RichTextArea() {
        super();

        textContainer.setAttribute("contenteditable", "true");
        addTool(btnBold, null, "editor-bold", Localization.get("bold"));
        addTool(btnItalic, null, "editor-italic", Localization.get("italic"));
        addTool(btnUnderline, null, "editor-underline", Localization.get("underline"));

        addTool(tglSubScript, groupSubSuperScript, "editor-subscript", Localization.get("subscript"));
        addTool(tglSuperScript, groupSubSuperScript, "editor-superscript", Localization.get("superscript"));

        addTool(tglAlignLeft, groupAlingment, "editor-align-left", Localization.get("justify.left"));
        addTool(tglAlignCenter, groupAlingment, "editor-align-center", Localization.get("justify.center"));
        addTool(tglAlignFull, groupAlingment, "editor-align-full", Localization.get("justify.center"));
        addTool(tglAlignRight, groupAlingment, "editor-align-right", Localization.get("justify.right"));

        addTool(btnCut, groupCcp, "editor-cut", Localization.get("cut"));
        addTool(btnCopy, groupCcp, "editor-copy", Localization.get("copy"));
        addTool(btnPaste, groupCcp, "editor-paste", Localization.get("paste"));

        addTool(btnUndo, groupUndoRedo, "editor-undo", Localization.get("undo"));
        addTool(btnRedo, groupUndoRedo, "editor-redo", Localization.get("redo"));

        addTool(btnStrikeTrough, null, "editor-strike-through", Localization.get("strike.through"));
        addTool(btnIdentRight, null, "editor-indent-right", Localization.get("indent"));
        addTool(btnIdentLeft, null, "editor-indent-left", Localization.get("outdent"));
        addTool(btnHorizontalRule, null, "editor-horisontal-rule", Localization.get("hr"));
        addTool(btnOrderedList, null, "editor-ordered-list", Localization.get("ol"));
        addTool(btnUnOrderedList, null, "editor-unordered-list", Localization.get("ul"));
        addTool(btnImage, null, "editor-insert-image", Localization.get("insert.image"));
        addTool(btnUploadImage, null, "editor-insert-image", Localization.get("upload.image"));
        addTool(btnCreateLink, null, "editor-create-link", Localization.get("create.link"));
        addTool(btnRemoveLink, null, "editor-delete-link", Localization.get("remove.link"));
        addTool(btnClearFormatting, null, "editor-remove-format", Localization.get("remove.format"));
        addTool(btnBackground, null, "editor-background-color", Localization.get("background.color"));
        addTool(btnForeground, null, "editor-foreground-color", Localization.get("foreground.color"));
        addControl(fonts, null, "");
        fonts.getElement().getStyle().setWidth(120, Style.Unit.PX);
        addControl(fontSizes, null, "");
        fontSizes.getElement().getStyle().setWidth(80, Style.Unit.PX);

        textContainer.setClassName("form-control");
        textContainer.addClassName(CommonResources.INSTANCE.commons().borderSized());
        element.appendChild(toolbar.getElement());
        element.appendChild(textContainer);

        btnBold.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('bold', null, null);
            }
        });

        btnItalic.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('italic', null, null);
            }
        });

        btnUnderline.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('underline', null, null);
            }
        });

        tglSubScript.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('subscript', null, null);
            }
        });

        tglSuperScript.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('superscript', null, null);
            }
        });

        tglAlignLeft.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('justifyLeft', null, null);
            }
        });

        tglAlignCenter.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('justifyCenter', null, null);
            }
        });
        tglAlignFull.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('justifyFull', null, null);
            }
        });

        tglAlignRight.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('justifyRight', null, null);
            }
        });

        btnCut.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('cut', null, null);
            }
        });

        btnCopy.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('copy', null, null);
            }
        });

        btnPaste.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('paste', null, null);
            }
        });

        btnStrikeTrough.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('strikeThrough', null, null);
            }
        });

        btnIdentRight.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('indent', null, null);
            }
        });

        btnIdentLeft.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('outdent', null, null);
            }
        });

        btnHorizontalRule.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('insertHorizontalRule', null, null);
            }
        });

        btnOrderedList.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('insertOrderedList', null, null);
            }
        });

        btnUnOrderedList.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('insertUnorderedList', null, null);
            }
        });

        btnBackground.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                JsUi.selectColor("#ffffff", new Callback<String, String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            // document.execCommand('backColor', null, result);
                        } catch (Exception ex) {
                            Logger.severe(ex);
                        }
                    }

                    @Override
                    public void onFailure(String reason) {
                    }
                });
            }
        });

        btnForeground.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                JsUi.selectColor("#ffffff", new Callback<String, String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            // document.execCommand('foreColor', null, result);
                        } catch (Exception ex) {
                            Logger.severe(ex);
                        }
                    }

                    @Override
                    public void onFailure(String reason) {
                    }
                });
            }
        });

        btnClearFormatting.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('removeFormat', null, result);
            }
        });

        btnCreateLink.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                /*
                    String url = prompt(constants.linkUrl(), "http://");
                    if (url != null) {
                        document.execCommand('createLink', null, url);
                    }
                */
            }
        });

        btnRemoveLink.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                // document.execCommand('unlink', null, null);
            }
        });

        btnImage.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                /*
                    String url = prompt(constants.imageUrl(), "http://");
                    if (url != null) {
                            document.execCommand('insertImage', null, url);
                    }
                */
            }
        });

        btnUploadImage.addActionHandler(new ActionHandler() {
            @Override
            public void onAction(ActionEvent event) {
                JsUi.selectFile(new Callback<JavaScriptObject, String>() {
                    @Override
                    public void onSuccess(JavaScriptObject result) {
                        /*
						try {
							if (result != null) {
								Cancellable cancellable = AppClient.getInstance().startUploadRequest((PublishedFile) result, ((PublishedFile) result).getName(), new Callback<ProgressEvent, String>() {
									protected boolean completed;

									public void onSuccess(ProgressEvent aResult) {
										try {
											if (!completed) {
												if (aResult.isComplete() && aResult.getRequest() != null) {
													completed = true;
													Utils.JsObject images = (Utils.JsObject) Utils.JsObject.parseJSON(aResult.getRequest().getResponseText());
													for (int i = 0; i < images.length(); i++){
														document.execCommand('insertImage', null, images.getString(i));
													}
												}
											}
										} catch (Exception ex) {
											Logger.severe(ex);
										}
									}

									public void onFailure(String reason) {
                                                                            Logger.severe(reason);
									}
								});
							} else
								return;
						} catch (Exception ex) {
							Logger.severe(ex);
						}
                         */
                    }

                    @Override
                    public void onFailure(String reason) {
                        Logger.severe(reason);
                    }
                }, "image/*");
            }
        });

        fontSizes.addValueChangeHandler(new ValueChangeHandler() {

            @Override
            public void onValueChange(ValueChangeEvent event) {
                //document.execCommand('fontSize', null, fontSizesConstants[fontSizes.getSelectedIndex() - 1]);
                fontSizes.setSelectedIndex(0);
            }
        });

        fonts.addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {
                //document.execCommand('fontName', null, fonts.getValue(fonts.getSelectedIndex()));
                fonts.setSelectedIndex(0);
            }
        });

        textContainer.<XElement>cast().addEventListener(BrowserEvents.BLUR, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Object oldValue = getValue();
                fireValueChange(oldValue);
                fireBlur();
            }
        });

        textContainer.<XElement>cast().addEventListener(BrowserEvents.FOCUS, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                fireFocus();
            }
        });

        textContainer.<XElement>cast().addEventListener(BrowserEvents.KEYDOWN, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                fireKeyDown(event);
            }
        });

        textContainer.<XElement>cast().addEventListener(BrowserEvents.KEYUP, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                fireKeyUp(event);
            }
        });

        textContainer.<XElement>cast().addEventListener(BrowserEvents.KEYPRESS, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                fireKeyPress(event);
            }
        });
    }

    public Flow getToolbar() {
        return toolbar;
    }

    public ImageButton getBtnBackground() {
        return btnBackground;
    }

    public ImageButton getBtnBold() {
        return btnBold;
    }

    public ImageButton getBtnClearFormatting() {
        return btnClearFormatting;
    }

    public ImageButton getBtnCopy() {
        return btnCopy;
    }

    public ImageButton getBtnCreateLink() {
        return btnCreateLink;
    }

    public ImageButton getBtnCut() {
        return btnCut;
    }

    public ImageButton getBtnForeground() {
        return btnForeground;
    }

    public ImageButton getBtnHorizontalRule() {
        return btnHorizontalRule;
    }

    public ImageButton getBtnIdentLeft() {
        return btnIdentLeft;
    }

    public ImageButton getBtnIdentRight() {
        return btnIdentRight;
    }

    public ImageButton getBtnImage() {
        return btnImage;
    }

    public ImageButton getBtnItalic() {
        return btnItalic;
    }

    public ImageButton getBtnOrderedList() {
        return btnOrderedList;
    }

    public ImageButton getBtnPaste() {
        return btnPaste;
    }

    public ImageButton getBtnRedo() {
        return btnRedo;
    }

    public ImageButton getBtnRemoveLink() {
        return btnRemoveLink;
    }

    public ImageButton getBtnStrikeTrough() {
        return btnStrikeTrough;
    }

    public ImageButton getBtnUnOrderedList() {
        return btnUnOrderedList;
    }

    public ImageButton getBtnUnderline() {
        return btnUnderline;
    }

    public ImageButton getBtnUndo() {
        return btnUndo;
    }

    public ImageButton getBtnUploadImage() {
        return btnUploadImage;
    }

    public ImageToggleButton getTglAlignCenter() {
        return tglAlignCenter;
    }

    public ImageToggleButton getTglAlignFull() {
        return tglAlignFull;
    }

    public ImageToggleButton getTglAlignLeft() {
        return tglAlignLeft;
    }

    public ImageToggleButton getTglAlignRight() {
        return tglAlignRight;
    }

    public ImageToggleButton getTglSubScript() {
        return tglSubScript;
    }

    public ImageToggleButton getTglSuperScript() {
        return tglSuperScript;
    }

    private void addControl(Widget aWidget, ButtonGroup aBtnGroup, String aClassName) {
        if (aClassName != null && !aClassName.isEmpty()) {
            aWidget.getElement().setClassName(aClassName);
        }
        if (aBtnGroup != null) {
            aBtnGroup.add(aWidget);
        }
        toolbar.add(aWidget);
    }

    private void addTool(Widget aWidget, ButtonGroup aBtnGroup, String divStyleName, String aTitle) {
        addControl(aWidget, aBtnGroup, divStyleName);
        aWidget.setTitle(aTitle);
    }

    //private static final RichTextArea.FontSize[] fontSizesConstants = new RichTextArea.FontSize[]{RichTextArea.FontSize.XX_SMALL, RichTextArea.FontSize.X_SMALL, RichTextArea.FontSize.SMALL,
    //    RichTextArea.FontSize.MEDIUM, RichTextArea.FontSize.LARGE, RichTextArea.FontSize.X_LARGE, RichTextArea.FontSize.XX_LARGE};
    private DropDownList createFontList() {
        DropDownList lb = new DropDownList();
        lb.setVisibleItemCount(1);

        lb.addItem(Localization.get("font"), "", null, null);
        // lb.addItem(strings.normal(), "", null, null);
        lb.addItem("Times New Roman", "Times New Roman", null, null);
        lb.addItem("Arial", "Arial", null, null);
        lb.addItem("Courier New", "Courier New", null, null);
        lb.addItem("Georgia", "Georgia", null, null);
        lb.addItem("Trebuchet", "Trebuchet", null, null);
        lb.addItem("Verdana", "Verdana", null, null);
        return lb;
    }

    private DropDownList createFontSizes() {
        DropDownList lb = new DropDownList();
        lb.setVisibleItemCount(1);
        lb.addItem(Localization.get("size"), null, null, null);
        lb.addItem(Localization.get("xxsmall"), null, null, null);
        lb.addItem(Localization.get("xsmall"), null, null, null);
        lb.addItem(Localization.get("small"), null, null, null);
        lb.addItem(Localization.get("medium"), null, null, null);
        lb.addItem(Localization.get("large"), null, null, null);
        lb.addItem(Localization.get("xlarge"), null, null, null);
        lb.addItem(Localization.get("xxlarge"), null, null, null);
        return lb;
    }

    private Set<FocusHandler> focusHandlers = new Set();

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        focusHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                focusHandlers.remove(handler);
            }

        };
    }

    private void fireFocus() {
        FocusEvent event = new FocusEvent(this);
        for (FocusHandler h : focusHandlers) {
            h.onFocus(event);
        }
    }

    private Set<BlurHandler> blurHandlers = new Set();

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        blurHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                blurHandlers.remove(handler);
            }

        };
    }

    private void fireBlur() {
        BlurEvent event = new BlurEvent(this);
        for (BlurHandler h : blurHandlers) {
            h.onBlur(event);
        }
    }

    private Set<KeyUpHandler> keyUpHandlers = new Set();

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        keyUpHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                keyUpHandlers.remove(handler);
            }

        };
    }

    private void fireKeyUp(NativeEvent nevent) {
        KeyUpEvent event = new KeyUpEvent(this, nevent);
        for (KeyUpHandler h : keyUpHandlers) {
            h.onKeyUp(event);
        }
    }

    private Set<KeyDownHandler> keyDownHandlers = new Set();

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        keyDownHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                keyDownHandlers.remove(handler);
            }

        };
    }

    private void fireKeyDown(NativeEvent nevent) {
        KeyDownEvent event = new KeyDownEvent(this, nevent);
        for (KeyDownHandler h : keyDownHandlers) {
            h.onKeyDown(event);
        }
    }

    private Set<KeyPressHandler> keyPressHandlers = new Set();

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        keyPressHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                keyPressHandlers.remove(handler);
            }

        };
    }

    private void fireKeyPress(NativeEvent nevent) {
        KeyPressEvent event = new KeyPressEvent(this, nevent);
        for (KeyPressHandler h : keyPressHandlers) {
            h.onKeyPress(event);
        }
    }

    @Override
    public String getEmptyText() {
        return emptyText;
    }

    @Override
    public void setEmptyText(String aValue) {
        emptyText = aValue;
        WidgetsUtils.applyEmptyText(getElement(), emptyText);
    }

    @Override
    public void setFocus(boolean aValue) {
        if (aValue) {
            textContainer.focus();
        } else {
            textContainer.blur();
        }
    }

    @Override
    public int getTabIndex() {
        return textContainer.getTabIndex();
    }

    @Override
    public void setTabIndex(int index) {
        textContainer.setTabIndex(index);
    }

    @Override
    public Object getValue() {
        return textContainer.getInnerHTML();
    }

    @Override
    public void setValue(Object aValue) {
        Object oldValue = getValue();
        textContainer.setInnerHTML((String) aValue);
        fireValueChange(oldValue);
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        Object.defineProperty(published, "value", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusHtmlEditor::getValue()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusHtmlEditor::setValue(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        //		Object.defineProperty(published, "text", {
        //			get : function() {
        //				return aWidget.@com.eas.widgets.PlatypusHtmlEditor::getText()();
        //			},
        //			set : function(aValue) {
        //				aWidget.@com.eas.widgets.PlatypusHtmlEditor::setText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
        //			}
        //		});
        Object.defineProperty(published, "emptyText", {
            get : function() {
                return aWidget.@com.eas.ui.HasEmptyText::getEmptyText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
            }
        });
    }-*/;
}
