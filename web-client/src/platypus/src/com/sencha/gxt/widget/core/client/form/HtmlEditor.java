/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RichTextArea.FontSize;
import com.google.gwt.user.client.ui.RichTextArea.Justification;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.button.CellButtonBase;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.ColorMenu;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * Provides an HTML-based rich text editor with a tool bar for selecting
 * formatting options, including fonts, text justification, lists, hyperlinks
 * and text color. Enables switching between formatted and HTML editing modes.
 * Supports copy and paste from Web pages as well as text editing features
 * provided by the browser (e.g. spell checking, text search).
 * <p/>
 * By default, all formatting options are enabled and available via the tool
 * bar. To disable one or more options, use the appropriate setter <b>before</b>
 * adding the HTML editor to its container.
 */
public class HtmlEditor extends AdapterField<String> {

  /**
   * The appearance of this class.
   */
  public interface HtmlEditorAppearance {

    ImageResource bold();

    String editor();

    ImageResource fontColor();

    ImageResource fontDecrease();

    ImageResource fontHighlight();

    ImageResource fontIncrease();

    String frame();

    Element getContentElement(XElement parent);

    ImageResource italic();

    ImageResource justifyCenter();

    ImageResource justifyLeft();

    ImageResource justifyRight();

    ImageResource link();

    ImageResource ol();

    ImageResource source();

    ImageResource ul();

    ImageResource underline();
  }

  /**
   * The locale-sensitive messages used by this class.
   */
  public interface HtmlEditorMessages {
    String backColorTipText();

    String backColorTipTitle();

    String boldTipText();

    String boldTipTitle();

    String createLinkText();

    String decreaseFontSizeTipText();

    String decreaseFontSizeTipTitle();

    String foreColorTipText();

    String foreColorTipTitle();

    String increaseFontSizeTipText();

    String increaseFontSizeTipTitle();

    String italicTipText();

    String italicTipTitle();

    String justifyCenterTipText();

    String justifyCenterTipTitle();

    String justifyLeftTipText();

    String justifyLeftTipTitle();

    String justifyRightTipText();

    String justifyRightTipTitle();

    String linkTipText();

    String linkTipTitle();

    String olTipText();

    String olTipTitle();

    String sourceEditTipText();

    String sourceEditTipTitle();

    String ulTipText();

    String ulTipTitle();

    String underlineTipText();

    String underlineTipTitle();
  }

  protected class HtmlEditorDefaultMessages implements HtmlEditorMessages {

    @Override
    public String backColorTipText() {
      return DefaultMessages.getMessages().htmlEditor_backColorTipText();
    }

    @Override
    public String backColorTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_backColorTipTitle();
    }

    @Override
    public String boldTipText() {
      return DefaultMessages.getMessages().htmlEditor_boldTipText();
    }

    @Override
    public String boldTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_boldTipTitle();
    }

    @Override
    public String createLinkText() {
      return DefaultMessages.getMessages().htmlEditor_createLinkText();
    }

    @Override
    public String decreaseFontSizeTipText() {
      return DefaultMessages.getMessages().htmlEditor_decreaseFontSizeTipText();
    }

    @Override
    public String decreaseFontSizeTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_decreaseFontSizeTipTitle();
    }

    @Override
    public String foreColorTipText() {
      return DefaultMessages.getMessages().htmlEditor_foreColorTipText();
    }

    @Override
    public String foreColorTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_foreColorTipTitle();
    }

    @Override
    public String increaseFontSizeTipText() {
      return DefaultMessages.getMessages().htmlEditor_increaseFontSizeTipText();
    }

    @Override
    public String increaseFontSizeTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_increaseFontSizeTipTitle();
    }

    @Override
    public String italicTipText() {
      return DefaultMessages.getMessages().htmlEditor_italicTipText();
    }

    @Override
    public String italicTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_italicTipTitle();
    }

    @Override
    public String justifyCenterTipText() {
      return DefaultMessages.getMessages().htmlEditor_justifyCenterTipText();
    }

    @Override
    public String justifyCenterTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_justifyCenterTipTitle();
    }

    @Override
    public String justifyLeftTipText() {
      return DefaultMessages.getMessages().htmlEditor_justifyLeftTipText();
    }

    @Override
    public String justifyLeftTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_justifyLeftTipTitle();
    }

    @Override
    public String justifyRightTipText() {
      return DefaultMessages.getMessages().htmlEditor_justifyRightTipText();
    }

    @Override
    public String justifyRightTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_justifyRightTipTitle();
    }

    @Override
    public String linkTipText() {
      return DefaultMessages.getMessages().htmlEditor_linkTipText();
    }

    @Override
    public String linkTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_linkTipTitle();
    }

    @Override
    public String olTipText() {
      return DefaultMessages.getMessages().htmlEditor_olTipText();
    }

    @Override
    public String olTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_olTipTitle();
    }

    @Override
    public String sourceEditTipText() {
      return DefaultMessages.getMessages().htmlEditor_sourceEditTipText();
    }

    @Override
    public String sourceEditTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_sourceEditTipTitle();
    }

    @Override
    public String ulTipText() {
      return DefaultMessages.getMessages().htmlEditor_ulTipText();
    }

    @Override
    public String ulTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_ulTipTitle();
    }

    @Override
    public String underlineTipText() {
      return DefaultMessages.getMessages().htmlEditor_underlineTipText();
    }

    @Override
    public String underlineTipTitle() {
      return DefaultMessages.getMessages().htmlEditor_underlineTipTitle();
    }

  }

  protected VerticalLayoutContainer container;
  protected ToolBar toolBar;
  protected final HtmlEditorAppearance appearance;
  protected HtmlEditorMessages messages;
  protected RichTextArea textArea;
  protected TextArea sourceTextArea;
  protected List<FontSize> fontSizesConstants = new ArrayList<FontSize>();
  protected FontSize activeFontSize = FontSize.SMALL;

  private boolean enableAlignments = true;
  private boolean enableColors = true;
  private boolean enableFont = true;
  private boolean enableFontSize = true;
  private boolean enableFormat = true;
  private boolean enableLinks = true;
  private boolean enableLists = true;
  private boolean showToolBar = true;
  private boolean sourceEditMode = true;
  private TextButton fontIncrease;
  private TextButton fontDecrease;
  private SelectHandler buttonHandler;
  private ToggleButton bold;
  private ToggleButton italic;
  private ToggleButton underline;
  private TextButton justifyLeft;
  private TextButton justifyCenter;
  private TextButton justifyRight;
  private TextButton ol;
  private TextButton ul;
  private TextButton link;
  private TextButton fontColor;
  private TextButton fontHighlight;
  private ToggleButton sourceEdit;

  /**
   * Creates an HTML-based rich text editor with support for fonts, text
   * justification, lists, hyperlinks and text color.
   */
  public HtmlEditor() {
    this(GWT.<HtmlEditorAppearance> create(HtmlEditorAppearance.class));
  }

  /**
   * Creates an HTML-based rich text editor with support for fonts, text
   * justification, lists, hyperlinks and text color.
   */
  public HtmlEditor(HtmlEditorAppearance appearance) {
    super(new VerticalLayoutContainer());
    this.container = (VerticalLayoutContainer) getWidget();
    this.appearance = appearance;

    addStyleName(appearance.editor());
    setBorders(true);

    toolBar = new ToolBar();

    textArea = new RichTextArea();
    textArea.addStyleName(appearance.frame());
    textArea.addFocusHandler(new FocusHandler() {

      @Override
      public void onFocus(FocusEvent event) {
        ensureTriggerFieldBlur();
      }
    });

    container.add(toolBar, new VerticalLayoutData(1, -1));
    container.add(textArea, new VerticalLayoutData(1, 1));

    fontSizesConstants.add(FontSize.XX_SMALL);
    fontSizesConstants.add(FontSize.X_SMALL);
    fontSizesConstants.add(FontSize.SMALL);
    fontSizesConstants.add(FontSize.MEDIUM);
    fontSizesConstants.add(FontSize.LARGE);
    fontSizesConstants.add(FontSize.X_LARGE);
    fontSizesConstants.add(FontSize.XX_LARGE);
  }

  /**
   * Returns the locale-sensitive messages used by this class.
   * 
   * @return the local-sensitive messages used by this class.
   */
  public HtmlEditorMessages getMessages() {
    if (messages == null) {
      messages = new HtmlEditorDefaultMessages();
    }
    return messages;
  }

  @Override
  public String getValue() {
    return textArea.getHTML();
  }

  /**
   * Returns true if text justification is enabled.
   * 
   * @return if text justification is enabled
   */
  public boolean isEnableAlignments() {
    return enableAlignments;
  }

  /**
   * Returns true if setting text foreground and background colors is enabled.
   * 
   * @return if setting text foreground and background colors is enabled
   */
  public boolean isEnableColors() {
    return enableColors;
  }

  /**
   * Returns true if setting font family name is enabled.
   * 
   * @return if setting font family name is enabled
   */
  public boolean isEnableFont() {
    return enableFont;
  }

  /**
   * Returns true if setting font size is enabled.
   * 
   * @return true if setting font size is enabled
   */
  public boolean isEnableFontSize() {
    return enableFontSize;
  }

  /**
   * Returns true if setting font style is enabled.
   * 
   * @return true if setting font style is enabled
   */
  public boolean isEnableFormat() {
    return enableFormat;
  }

  /**
   * Returns true if creating a hyperlink from selected text is enabled.
   * 
   * @return true if creating a hyperlink from selected text is enabled
   */
  public boolean isEnableLinks() {
    return enableLinks;
  }

  /**
   * Returns true if creating lists is enabled.
   * 
   * @return true if creating lists is enabled
   */
  public boolean isEnableLists() {
    return enableLists;
  }

  /**
   * Returns {@code true} if the toolbar is displayed.
   * 
   * @return {@code true} if the toolbar is displayed
   */
  public boolean isShowToolBar() {
    return showToolBar;
  }

  /**
   * Returns true if the ability to switch to HTML source mode is enabled.
   * 
   * @return true if the ability to switch to HTML source mode is enabled
   */
  public boolean isSourceEditMode() {
    return sourceEditMode;
  }

  /**
   * Copies the value of the HTML source editor to the rich text editor.
   */
  public void pushValue() {
    textArea.setHTML(sourceTextArea.getValue());
  }

  /**
   * Sets whether text justification is enabled.
   * 
   * @param enableAlignments true to enable text justification
   */
  public void setEnableAlignments(boolean enableAlignments) {
    this.enableAlignments = enableAlignments;
  }

  /**
   * Sets whether setting text foreground and background colors is enabled.
   * 
   * @param enableColors true to enable setting text foreground and background
   *          colors.
   */
  public void setEnableColors(boolean enableColors) {
    this.enableColors = enableColors;
  }

  /**
   * Sets whether setting font family name is enabled.
   * 
   * @param enableFont true to enable setting font family name
   */
  public void setEnableFont(boolean enableFont) {
    this.enableFont = enableFont;
  }

  /**
   * Sets whether setting font size is enabled.
   * 
   * @param enableFontSize true to enable setting font size
   */
  public void setEnableFontSize(boolean enableFontSize) {
    this.enableFontSize = enableFontSize;
  }

  /**
   * Sets whether setting font style is enabled.
   * 
   * @param enableFormat true to enable setting font style
   */
  public void setEnableFormat(boolean enableFormat) {
    this.enableFormat = enableFormat;
  }

  /**
   * Sets whether creating a hyperlink from selected text is enable.
   * 
   * @param enableLinks true to enable creating a hyperlink from selected text
   */
  public void setEnableLinks(boolean enableLinks) {
    this.enableLinks = enableLinks;
  }

  /**
   * Sets whether creating lists is enabled.
   * 
   * @param enableLists true to enable creating lists
   */
  public void setEnableLists(boolean enableLists) {
    this.enableLists = enableLists;
  }

  /**
   * Sets the local-sensitive messages used by this class.
   * 
   * @param messages the locale sensitive messages used by this class.
   */
  public void setMessages(HtmlEditorMessages messages) {
    this.messages = messages;
  }

  /**
   * Sets whether the toolbar should be shown. This property can only be
   * modified before this component is first attached.
   * 
   * @param showToolBar {@code true} to show the toolbar, {@code false} otherwise.
   */
  public void setShowToolBar(boolean showToolBar) {
    assertPreRender();
    this.showToolBar = showToolBar;
  }

  @Override
  public void setValue(String value) {
    textArea.setHTML(value);
  }

  /**
   * Copies the value of the rich text editor to the HTML source editor.
   */
  public void syncValue() {
    sourceTextArea.setValue(getValue());
  }

  protected void initToolBar() {
    if (!showToolBar) {
      return;
    }

    buttonHandler = new SelectHandler() {

      @Override
      public void onSelect(SelectEvent event) {
        Widget button = (Widget) event.getSource();

        if (button == fontIncrease) {
          int i = fontSizesConstants.indexOf(activeFontSize);
          if (i < (fontSizesConstants.size() - 1)) {
            i++;
            activeFontSize = fontSizesConstants.get(i);
            textArea.getFormatter().setFontSize(activeFontSize);
          } else {
            // brings focus back to the editor
            focus();
          }
        } else if (button == fontDecrease) {
          int i = fontSizesConstants.indexOf(activeFontSize);
          if (i > 0) {
            i--;
            activeFontSize = fontSizesConstants.get(i);
            textArea.getFormatter().setFontSize(activeFontSize);
          } else {
            // brings focus back to the editor
            focus();
          }
        } else if (button == bold) {
          textArea.getFormatter().toggleBold();
        } else if (button == italic) {
          textArea.getFormatter().toggleItalic();
        } else if (button == underline) {
          textArea.getFormatter().toggleUnderline();
        } else if (button == justifyLeft) {
          textArea.getFormatter().setJustification(Justification.LEFT);
        } else if (button == justifyCenter) {
          textArea.getFormatter().setJustification(Justification.CENTER);
        } else if (button == justifyRight) {
          textArea.getFormatter().setJustification(Justification.RIGHT);
        } else if (button == ol) {
          textArea.getFormatter().insertOrderedList();
        } else if (button == ul) {
          textArea.getFormatter().insertUnorderedList();
        } else if (button == link) {
          String link = Window.prompt(getMessages().createLinkText(), "http://");
          if (link != null && link.length() > 0) {
            textArea.getFormatter().createLink(link);
          } else {
            textArea.getFormatter().removeLink();
          }
        }
      }
    };

    HtmlEditorMessages m = getMessages();

    if (enableFont) {
      final ListBox fonts = new ListBox();
      fonts.addItem("Arial");
      fonts.addItem("Courier New");
      fonts.addItem("Times New Roman");
      fonts.addItem("Verdana");
      fonts.setItemSelected(0, true);

      fonts.addChangeHandler(new ChangeHandler() {

        public void onChange(ChangeEvent event) {
          int index = fonts.getSelectedIndex();
          if (index != 0) {
            textArea.getFormatter().setFontName(fonts.getItemText(index));
          }
        }
      });

      toolBar.add(fonts);
      toolBar.add(new SeparatorToolItem());
    }

    if (enableFontSize) {
      fontIncrease = new TextButton();
      configureButton(fontIncrease, appearance.fontIncrease(), m.increaseFontSizeTipTitle(),
          m.increaseFontSizeTipText());
      fontIncrease.addSelectHandler(buttonHandler);
      toolBar.add(fontIncrease);

      fontDecrease = new TextButton();
      configureButton(fontDecrease, appearance.fontDecrease(), m.decreaseFontSizeTipTitle(),
          m.decreaseFontSizeTipText());
      fontDecrease.addSelectHandler(buttonHandler);
      toolBar.add(fontDecrease);

      toolBar.add(new SeparatorToolItem());
    }

    if (enableFormat) {
      bold = new ToggleButton();
      configureButton(bold, appearance.bold(), m.boldTipTitle(), m.boldTipText());
      bold.addSelectHandler(buttonHandler);
      toolBar.add(bold);

      italic = new ToggleButton();
      configureButton(italic, appearance.italic(), m.italicTipTitle(), m.italicTipText());
      italic.addSelectHandler(buttonHandler);
      toolBar.add(italic);

      underline = new ToggleButton();
      configureButton(underline, appearance.underline(), m.underlineTipTitle(), m.underlineTipText());
      underline.addSelectHandler(buttonHandler);
      toolBar.add(underline);

      toolBar.add(new SeparatorToolItem());
    }

    if (enableAlignments) {
      justifyLeft = new TextButton();
      configureButton(justifyLeft, appearance.justifyLeft(), m.justifyLeftTipTitle(), m.justifyLeftTipText());
      justifyLeft.addSelectHandler(buttonHandler);
      toolBar.add(justifyLeft);

      justifyCenter = new TextButton();
      configureButton(justifyCenter, appearance.justifyCenter(), m.justifyCenterTipTitle(), m.justifyCenterTipText());
      justifyCenter.addSelectHandler(buttonHandler);
      toolBar.add(justifyCenter);

      justifyRight = new TextButton();
      configureButton(justifyRight, appearance.justifyRight(), m.justifyRightTipTitle(), m.justifyRightTipText());
      justifyRight.addSelectHandler(buttonHandler);
      toolBar.add(justifyRight);

      toolBar.add(new SeparatorToolItem());
    }

    if (enableLists) {
      ol = new TextButton();
      configureButton(ol, appearance.ol(), m.olTipTitle(), m.olTipText());
      ol.addSelectHandler(buttonHandler);
      toolBar.add(ol);

      ul = new TextButton();
      configureButton(ul, appearance.ul(), m.ulTipTitle(), m.ulTipText());
      ul.addSelectHandler(buttonHandler);
      toolBar.add(ul);

      toolBar.add(new SeparatorToolItem());
    }

    if (enableLinks) {
      link = new TextButton();
      configureButton(link, appearance.link(), m.linkTipTitle(), m.linkTipText());
      link.addSelectHandler(buttonHandler);
      toolBar.add(link);

      toolBar.add(new SeparatorToolItem());
    }

    if (enableColors) {
      fontColor = new TextButton();
      configureButton(fontColor, appearance.fontColor(), m.foreColorTipTitle(), m.foreColorTipText());
      ColorMenu menu = new ColorMenu();
      menu.getPalette().addValueChangeHandler(new ValueChangeHandler<String>() {

        @Override
        public void onValueChange(ValueChangeEvent<String> event) {
          textArea.getFormatter().setForeColor(event.getValue());
        }
      });
      fontColor.setMenu(menu);

      toolBar.add(fontColor);

      fontHighlight = new TextButton();
      configureButton(fontHighlight, appearance.fontHighlight(), m.backColorTipTitle(), m.backColorTipText());

      menu = new ColorMenu();
      menu.getPalette().addValueChangeHandler(new ValueChangeHandler<String>() {
        @Override
        public void onValueChange(ValueChangeEvent<String> event) {
          textArea.getFormatter().setBackColor(event.getValue());
        }
      });
      fontHighlight.setMenu(menu);

      toolBar.add(fontHighlight);
    }

    if (sourceEditMode) {
      toolBar.add(new SeparatorToolItem());
      sourceEdit = new ToggleButton();
      configureButton(sourceEdit, appearance.source(), m.sourceEditTipTitle(), m.sourceEditTipText());

      sourceEdit.addSelectHandler(new SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
          toggleSourceEditMode();
          focus();
        }
      });
      toolBar.add(sourceEdit);

    }
  }

  @Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();
    initToolBar();
  }

  @Override
  protected void onDisable() {
    super.onDisable();
    getElement().selectNode("textarea").disable();
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    getElement().selectNode("textarea").enable();
  }

  protected void toggleSourceEditMode() {
    if (sourceEdit.getValue()) {
      if (sourceTextArea == null) {
        sourceTextArea = new TextArea();
      }

      sourceTextArea.setHeight(textArea.getOffsetHeight());

      syncValue();
      container.remove(1);
      container.add(sourceTextArea, new VerticalLayoutData(1, 1));
      container.forceLayout();
    } else {
      pushValue();
      container.remove(1);
      container.add(textArea, new VerticalLayoutData(1, 1));
      container.forceLayout();
    }

    for (int i = 0; i < toolBar.getWidgetCount(); i++) {
      Widget w = toolBar.getWidget(i);
      if (w != sourceEdit && w instanceof Component) {
        Component c = (Component) w;
        if (c.getData("gxt-more") != null) {
          continue;
        }
        c.setEnabled(!sourceEdit.getValue());
      } else {
        if (w instanceof FocusWidget) {
          ((FocusWidget) w).setEnabled(!sourceEdit.getValue());
        }
      }
    }
  }

  private void configureButton(CellButtonBase<?> button, ImageResource icon, String tipTitle, String tipText) {
    button.setIcon(icon);
    button.setToolTipConfig(createTipConfig(tipTitle, tipText));
    button.setData("gxt-menutext", tipTitle);
  }

  private ToolTipConfig createTipConfig(String title, String text) {
    return new ToolTipConfig(title, text);
  }

  private native void ensureTriggerFieldBlur() /*-{
		@com.sencha.gxt.cell.core.client.form.TriggerFieldCell::ensureBlur()();
  }-*/;

}
