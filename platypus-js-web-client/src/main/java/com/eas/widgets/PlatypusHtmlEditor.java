package com.eas.widgets;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.AppClient;
import com.eas.client.published.PublishedFile;
import com.eas.client.xhr.ProgressEvent;
import com.eas.core.Cancellable;
import com.eas.core.HasPublished;
import com.eas.core.Utils;
import com.eas.core.XElement;
import com.eas.menu.HasComponentPopupMenu;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.ui.ButtonGroup;
import com.eas.ui.CommonResources;
import com.eas.ui.HasEmptyText;
import com.eas.ui.HasEventsExecutor;
import com.eas.ui.HasJsFacade;
import com.eas.ui.JsUi;
import com.eas.ui.events.EventsExecutor;
import com.eas.ui.events.HasHideHandlers;
import com.eas.ui.events.HasShowHandlers;
import com.eas.ui.events.HideEvent;
import com.eas.ui.events.HideHandler;
import com.eas.ui.events.ShowEvent;
import com.eas.ui.events.ShowHandler;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.eas.ui.HasScroll;
import com.eas.ui.HorizontalScrollFiller;
import com.eas.ui.VerticalScrollFiller;

public class PlatypusHtmlEditor extends Composite implements HasScroll, HorizontalScrollFiller, VerticalScrollFiller, HasJsFacade, HasValueChangeHandlers<String>, HasEmptyText, HasValue<String>, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers,
        HasHideHandlers, HasResizeHandlers, RequiresResize, HasFocusHandlers, HasBlurHandlers, Focusable, HasEnabled, HasKeyDownHandlers, HasKeyPressHandlers, HasKeyUpHandlers {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String emptyText;
	protected String name;
	protected JavaScriptObject published;
	private boolean isNull = true;
	private String oldValue;
	protected boolean enabled = true;

	protected FlowPanel container = new FlowPanel();
	protected RichTextArea textContainer = new RichTextArea();
	protected ToolBar toolBar = new ToolBar();

	protected PlatypusButton btnBold = new PlatypusButton();
	protected PlatypusButton btnItalic = new PlatypusButton();
	protected PlatypusButton btnUnderline = new PlatypusButton();
	protected ButtonGroup groupSubSuperScript = new ButtonGroup();
	protected PlatypusToggleButton tglSubScript = new PlatypusToggleButton();
	protected PlatypusToggleButton tglSuperScript = new PlatypusToggleButton();
	protected ButtonGroup groupAlingment = new ButtonGroup();
	protected PlatypusToggleButton tglAlignLeft = new PlatypusToggleButton();
	protected PlatypusToggleButton tglAlignCenter = new PlatypusToggleButton();
	protected PlatypusToggleButton tglAlignRight = new PlatypusToggleButton();
	protected PlatypusButton btnBackground = new PlatypusButton();
	protected PlatypusButton btnForeground = new PlatypusButton();
	protected PlatypusButton btnStrikeTrough = new PlatypusButton();
	protected PlatypusButton btnIdentRight = new PlatypusButton();
	protected PlatypusButton btnIdentLeft = new PlatypusButton();
	protected PlatypusButton btnHorizontalRule = new PlatypusButton();
	protected PlatypusButton btnOrderedList = new PlatypusButton();
	protected PlatypusButton btnUnOrderedList = new PlatypusButton();
	protected PlatypusButton btnImage = new PlatypusButton();
	protected PlatypusButton btnUploadImage = new PlatypusButton();
	protected PlatypusButton btnCreateLink = new PlatypusButton();
	protected PlatypusButton btnRemoveLink = new PlatypusButton();
	protected ListBox fonts = createFontList();
	protected ListBox fontSizes = createFontSizes();
	protected PlatypusButton btnClearFormatting = new PlatypusButton();
	protected SimplePanel paneForRichTextBox = new SimplePanel();

	protected int elementWidth = 30;
	protected int elementHeight = 30;
	protected static PlatypusHtmlEditorConstants constants = GWT.create(PlatypusHtmlEditorConstants.class);

	public PlatypusHtmlEditor() {
		super();

		placeWidgetToMenu(btnBold, null, "editor-bold", constants.bold());
		placeWidgetToMenu(btnItalic, null, "editor-italic", constants.italic());
		placeWidgetToMenu(btnUnderline, null, "editor-underline", constants.underline());
		placeWidgetToMenu(tglSubScript, groupSubSuperScript, "editor-subscript", constants.subscript());
		placeWidgetToMenu(tglSuperScript, groupSubSuperScript, "editor-superscript", constants.superscript());
		placeWidgetToMenu(tglAlignLeft, groupAlingment, "editor-align-left", constants.justifyLeft());
		placeWidgetToMenu(tglAlignCenter, groupAlingment, "editor-align-center", constants.justifyCenter());
		placeWidgetToMenu(tglAlignRight, groupAlingment, "editor-align-right", constants.justifyRight());
		placeWidgetToMenu(btnStrikeTrough, null, "editor-strike-through", constants.strikeThrough());
		placeWidgetToMenu(btnIdentRight, null, "editor-indent-right", constants.indent());
		placeWidgetToMenu(btnIdentLeft, null, "editor-indent-left", constants.outdent());
		placeWidgetToMenu(btnHorizontalRule, null, "editor-horisontal-rule", constants.hr());
		placeWidgetToMenu(btnOrderedList, null, "editor-ordered-list", constants.ol());
		placeWidgetToMenu(btnUnOrderedList, null, "editor-unordered-list", constants.ul());
		placeWidgetToMenu(btnImage, null, "editor-insert-image", constants.insertImage());
		placeWidgetToMenu(btnUploadImage, null, "editor-insert-image", constants.uploadImage());
		placeWidgetToMenu(btnCreateLink, null, "editor-create-link", constants.createLink());
		placeWidgetToMenu(btnRemoveLink, null, "editor-delete-link", constants.removeLink());
		placeWidgetToMenu(btnClearFormatting, null, "editor-remove-format", constants.removeFormat());
		placeWidgetToMenu(btnBackground, null, "editor-background-color", constants.backgroundColor());
		placeWidgetToMenu(btnForeground, null, "editor-foreground-color", constants.foregroundColor());
		placeWidgetToMenu(fonts, null, "");
		fonts.getElement().getStyle().setWidth(120, Style.Unit.PX);
		placeWidgetToMenu(fontSizes, null, "");
		fontSizes.getElement().getStyle().setWidth(80, Style.Unit.PX);

		container.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		container.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		container.getElement().getStyle().setWidth(100, Style.Unit.PCT);

		textContainer.setStyleName("form-control");
		textContainer.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		textContainer.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		textContainer.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
		paneForRichTextBox.getElement().getStyle().setTop(32, Style.Unit.PX);
		paneForRichTextBox.getElement().getStyle().setBottom(0, Style.Unit.PX);
		paneForRichTextBox.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		paneForRichTextBox.getElement().getStyle().setProperty("position", "absolute");
		toolBar.getElement().getStyle().setHeight(elementHeight, Style.Unit.PX);
		toolBar.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		paneForRichTextBox.add(textContainer);
		container.add(toolBar);
		container.add(paneForRichTextBox);

		initWidget(container);

		btnBold.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().toggleBold();
			}
		});

		btnItalic.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().toggleItalic();
			}
		});

		btnUnderline.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().toggleUnderline();
			}
		});

		tglSubScript.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().toggleSubscript();
			}
		});

		tglSuperScript.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().toggleSuperscript();
			}
		});

		tglAlignLeft.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().setJustification(RichTextArea.Justification.LEFT);
			}
		});

		tglAlignCenter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().setJustification(RichTextArea.Justification.CENTER);
			}
		});

		tglAlignRight.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().setJustification(RichTextArea.Justification.RIGHT);
			}
		});

		btnStrikeTrough.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().toggleStrikethrough();
			}
		});

		btnIdentRight.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().rightIndent();
			}
		});

		btnIdentLeft.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().leftIndent();
			}
		});

		btnHorizontalRule.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().insertHorizontalRule();
			}
		});

		btnOrderedList.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().insertOrderedList();
			}
		});

		btnUnOrderedList.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().insertUnorderedList();
			}
		});

		btnBackground.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				JsUi.selectColor(textContainer.getFormatter().getBackColor(), new Callback<String, String>() {
					@Override
					public void onSuccess(String result) {
						try {
							textContainer.getFormatter().setBackColor(result);
						} catch (Exception ex) {
							Logger.getLogger(WidgetsUtils.class.getName()).log(Level.SEVERE, null, ex);
						}
					}

					@Override
					public void onFailure(String reason) {
					}
				});
			}
		});

		btnForeground.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				JsUi.selectColor(textContainer.getFormatter().getForeColor(), new Callback<String, String>() {
					@Override
					public void onSuccess(String result) {
						try {
							textContainer.getFormatter().setForeColor(result);
						} catch (Exception ex) {
							Logger.getLogger(WidgetsUtils.class.getName()).log(Level.SEVERE, null, ex);
						}
					}

					@Override
					public void onFailure(String reason) {
					}
				});
			}
		});

		btnClearFormatting.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().removeFormat();
			}
		});

		btnCreateLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String url = Window.prompt(constants.linkUrl(), "http://");
				if (url != null) {
					textContainer.getFormatter().createLink(url);
				}
			}
		});

		btnRemoveLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textContainer.getFormatter().removeLink();
			}
		});

		btnImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String url = Window.prompt(constants.imageUrl(), "http://");
				if (url != null) {
					textContainer.getFormatter().insertImage(url);
				}
			}
		});

		btnUploadImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				JsUi.selectFile(new Callback<JavaScriptObject, String>() {
					@Override
					public void onSuccess(JavaScriptObject result) {
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
														textContainer.getFormatter().insertImage(images.getString(i));
													}
												}
											}
										} catch (Exception ex) {
											Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
										}
									}

									public void onFailure(String reason) {
										Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, reason);
									}
								});
							} else
								return;
						} catch (Exception ex) {
							Logger.getLogger(WidgetsUtils.class.getName()).log(Level.SEVERE, null, ex);
						}
					}

					@Override
					public void onFailure(String reason) {
						Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, reason);
					}
				}, "image/*");
			}
		});

		fontSizes.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				textContainer.getFormatter().setFontSize(fontSizesConstants[fontSizes.getSelectedIndex() - 1]);
				fontSizes.setSelectedIndex(0);
			}
		});

		fonts.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				textContainer.getFormatter().setFontName(fonts.getValue(fonts.getSelectedIndex()));
				fonts.setSelectedIndex(0);
			}
		});

		getElement().<XElement> cast().addResizingTransitionEnd(this);

		textContainer.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				String newValue = getValue();
				if (oldValue == null ? newValue != null : !oldValue.equals(newValue)) {
					isNull = newValue == null;
					oldValue = newValue;
					ValueChangeEvent.fire(PlatypusHtmlEditor.this, newValue);
				}
				BlurEvent.fireNativeEvent(event.getNativeEvent(), PlatypusHtmlEditor.this);
			}
		});

		textContainer.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				toolBar.hideOverflowPopup();
				FocusEvent.fireNativeEvent(event.getNativeEvent(), PlatypusHtmlEditor.this);
			}
		});

		textContainer.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				KeyDownEvent.fireNativeEvent(event.getNativeEvent(), PlatypusHtmlEditor.this);
			}
		});

		textContainer.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				KeyUpEvent.fireNativeEvent(event.getNativeEvent(), PlatypusHtmlEditor.this);
			}
		});

		textContainer.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				KeyPressEvent.fireNativeEvent(event.getNativeEvent(), PlatypusHtmlEditor.this);
			}
		});

	}

	private void placeWidgetToMenu(Widget aWidget, ButtonGroup aBtnGroup, String aStyleName) {
		aWidget.getElement().getStyle().setWidth(elementWidth, Style.Unit.PX);
		aWidget.getElement().getStyle().setHeight(elementHeight, Style.Unit.PX);
		if (aStyleName != null && !aStyleName.isEmpty()) {
			aWidget.addStyleName(aStyleName);
		}
		if (aBtnGroup != null) {
			if (aWidget instanceof HasValue) {
				aBtnGroup.add((HasValue<Boolean>) aWidget);
			}
		}
		toolBar.add(aWidget);
	}

	private void placeWidgetToMenu(Widget aWidget, ButtonGroup aBtnGroup, String divStyleName, String aTitle) {
		placeWidgetToMenu(aWidget, aBtnGroup, divStyleName);
		aWidget.setTitle(aTitle);

	}

	private void addDivToElement(Element aBase, String aName) {
		if (aName.length() > 0) {
			Element imageDiv = Document.get().createDivElement();
			imageDiv.addClassName(aName);
			aBase.appendChild(imageDiv);
		}
	}

	private static final RichTextArea.FontSize[] fontSizesConstants = new RichTextArea.FontSize[] { RichTextArea.FontSize.XX_SMALL, RichTextArea.FontSize.X_SMALL, RichTextArea.FontSize.SMALL,
	        RichTextArea.FontSize.MEDIUM, RichTextArea.FontSize.LARGE, RichTextArea.FontSize.X_LARGE, RichTextArea.FontSize.XX_LARGE };

	private ListBox createFontList() {
		ListBox lb = new ListBox();
		lb.setVisibleItemCount(1);

		lb.addItem(constants.font(), "");
		// lb.addItem(strings.normal(), "");
		lb.addItem("Times New Roman", "Times New Roman");
		lb.addItem("Arial", "Arial");
		lb.addItem("Courier New", "Courier New");
		lb.addItem("Georgia", "Georgia");
		lb.addItem("Trebuchet", "Trebuchet");
		lb.addItem("Verdana", "Verdana");
		return lb;
	}

	private ListBox createFontSizes() {
		ListBox lb = new ListBox();
		lb.setVisibleItemCount(1);
		lb.addItem(constants.size());
		lb.addItem(constants.xxsmall());
		lb.addItem(constants.xsmall());
		lb.addItem(constants.small());
		lb.addItem(constants.medium());
		lb.addItem(constants.large());
		lb.addItem(constants.xlarge());
		lb.addItem(constants.xxlarge());
		return lb;
	}

	@Override
	public String getValue() {
		return isNull ? null : textContainer.getHTML();
	}

	@Override
	public void setValue(String value) {
		setValue(value, true);
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		oldValue = getValue();
		isNull = value == null;
		if (isNull) {
			value = "";
		}
		SafeHtml html = SimpleHtmlSanitizer.sanitizeHtml(value);
		textContainer.setHTML(html);
		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, oldValue, getValue());
		}
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public void onResize() {
		if (isAttached()) {
			toolBar.onResize();
			ResizeEvent.fire(this, getElement().getOffsetWidth(), getElement().getOffsetHeight());
		}
	}

	@Override
	public HandlerRegistration addHideHandler(HideHandler handler) {
		return addHandler(handler, HideEvent.getType());
	}

	@Override
	public HandlerRegistration addShowHandler(ShowHandler handler) {
		return addHandler(handler, ShowEvent.getType());
	}

	@Override
	public void setVisible(boolean visible) {
		boolean oldValue = isVisible();
		super.setVisible(visible);
		if (oldValue != visible) {
			if (visible) {
				ShowEvent.fire(this, this);
			} else {
				HideEvent.fire(this, this);
			}
		}
	}

	@Override
	public EventsExecutor getEventsExecutor() {
		return eventsExecutor;
	}

	@Override
	public void setEventsExecutor(EventsExecutor aExecutor) {
		eventsExecutor = aExecutor;
	}

	@Override
	public PlatypusPopupMenu getPlatypusPopupMenu() {
		return menu;
	}

	protected HandlerRegistration menuTriggerReg;

	@Override
	public void setPlatypusPopupMenu(PlatypusPopupMenu aMenu) {
		if (menu != aMenu) {
			if (menuTriggerReg != null)
				menuTriggerReg.removeHandler();
			menu = aMenu;
			if (menu != null) {
				menuTriggerReg = super.addDomHandler(new ContextMenuHandler() {

					@Override
					public void onContextMenu(ContextMenuEvent event) {
						event.preventDefault();
						event.stopPropagation();
						menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
						menu.show();
					}
				}, ContextMenuEvent.getType());
			}
		}
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
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

	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
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

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return addHandler(handler, FocusEvent.getType());
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return addHandler(handler, BlurEvent.getType());
	}

	@Override
	public void setFocus(boolean focused) {
		textContainer.setFocus(focused);

	}

	@Override
	public void setAccessKey(char key) {
		textContainer.setAccessKey(key);
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
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean aValue) {
		boolean oldValue = enabled;
		enabled = aValue;
		if (!oldValue && enabled) {
			getElement().<XElement> cast().unmask();
		} else if (oldValue && !enabled) {
			getElement().<XElement> cast().disabledMask();
		}
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return addHandler(handler, KeyUpEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return addHandler(handler, KeyPressEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return addHandler(handler, KeyDownEvent.getType());
	}

}
