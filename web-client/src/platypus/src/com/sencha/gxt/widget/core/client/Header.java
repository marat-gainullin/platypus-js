/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;

/**
 * A custom widget that supports an icon, text, and tool area.
 */
public class Header extends Component implements HasIcon, HasText, HasHTML, HasSafeHtml {

  /**
   * The appearance of a header. A header has a bar that contains an icon and
   * text.
   */
  public interface HeaderAppearance {

    /**
     * Returns the bar element for the specified header.
     * 
     * @param parent the header root element
     * @return the bar element
     */
    XElement getBarElem(XElement parent);

    /**
     * Returns the text element for the specified header.
     * 
     * @param parent the header root element
     * @return the text element
     */
    XElement getTextElem(XElement parent);

    /**
     * Renders the appearance of a header as HTML into a {@link SafeHtmlBuilder}
     * suitable for passing to {@link Element#setInnerHTML(String)} on a
     * container element.
     * 
     * @param sb receives the rendered appearance
     */
    void render(SafeHtmlBuilder sb);

    /**
     * Sets the icon for the specified header.
     * 
     * @param parent the header root element
     * @param icon the icon to display in the header
     */
    void setIcon(XElement parent, ImageResource icon);

  }

  protected ImageResource icon;

  private List<Widget> tools = new ArrayList<Widget>();
  private HorizontalPanel widgetPanel;
  private String text, altIconText;
  private HeaderAppearance appearance;

  /**
   * Creates a header with the default appearance which includes a header bar,
   * text and an icon.
   */
  public Header() {
    this(GWT.<HeaderAppearance> create(HeaderAppearance.class));
  }

  /**
   * Creates a header with the specified appearance.
   * 
   * @param appearance the appearance of the header
   */
  public Header(HeaderAppearance appearance) {
    this.appearance = appearance;

    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    this.appearance.render(sb);

    setElement(XDOM.create(sb.toSafeHtml()));

    addStyleName("x-small-editor");

    widgetPanel = new HorizontalPanel();
    widgetPanel.addStyleName("x-panel-toolbar");

    XElement barElem = appearance.getBarElem(getElement());
    barElem.appendChild(widgetPanel.getElement());

    if (tools.size() > 0) {
      for (int i = 0; i < tools.size(); i++) {
        widgetPanel.add(tools.get(i));
      }
    } else {
      widgetPanel.setVisible(false);
    }

    ComponentHelper.setParent(this, widgetPanel);

    appearance.getTextElem(getElement()).setId(getId() + "-label");

    setText(text);

    if (icon != null) {
      setIcon(icon);
    }

    getFocusSupport().setIgnore(true);
  }

  /**
   * Adds a tool.
   * 
   * @param tool the tool to be inserted
   */
  public void addTool(Widget tool) {
    insertTool(tool, getToolCount());
  }

  /**
   * Returns the appearance used to render the header.
   * 
   * @return the header appearance
   */
  public HeaderAppearance getAppearance() {
    return appearance;
  }

  @Override
  public String getHTML() {
    return appearance.getTextElem(getElement()).getInnerHTML();
  }

  /**
   * Returns the icon image
   * 
   * @return the icon
   */
  public ImageResource getIcon() {
    return icon;
  }

  /**
   * Returns the icon's alt text.
   * 
   * @return the alt text
   */
  public String getIconAltText() {
    return altIconText;
  }

  /**
   * Returns the header's text.
   * 
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * Returns the tool at the given index.
   * 
   * @param index the index
   * @return the tool
   */
  public Widget getTool(int index) {
    return tools.get(index);
  }

  /**
   * Returns the number of tool items.
   * 
   * @return the count
   */
  public int getToolCount() {
    return tools.size();
  }

  /**
   * Returns the tool's.
   * 
   * @return the tools
   */
  public List<Widget> getTools() {
    return tools;
  }

  /**
   * Returns the index of the given tool.
   * 
   * @param tool the tool
   * @return the index or -1 if no match
   */
  public int indexOf(Widget tool) {
    return tools.indexOf(tool);
  }

  /**
   * Inserts a tool.
   * 
   * @param tool the tool to insert
   * @param index the insert location
   */
  public void insertTool(Widget tool, int index) {
    tools.add(index, tool);
    widgetPanel.setVisible(true);
    widgetPanel.insert(tool, index);
  }

  /**
   * Removes a tool.
   * 
   * @param tool the tool to remove
   */
  public void removeTool(Widget tool) {
    tools.remove(tool);
    widgetPanel.remove(tool);
  }

  public void setHTML(SafeHtml html) {
    setHTML(html.asString());
  }

  @Override
  public void setHTML(String html) {
    appearance.getTextElem(getElement()).setInnerHTML(html);
  }

  @Override
  public void setIcon(ImageResource icon) {
    this.icon = icon;
    appearance.setIcon(getElement(), icon);
  }

  /**
   * Sets the header's icon alt text (defaults to null).
   * 
   * @param altIconText the icon alt text
   */
  public void setIconAltText(String altIconText) {
    this.altIconText = altIconText;
  }

  /**
   * Sets the header's text.
   * 
   * @param text the new text
   */
  public void setText(String text) {
    this.text = text;
    appearance.getTextElem(getElement()).setInnerHTML(text == null ? "&#160;" : text);
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(widgetPanel);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(widgetPanel);
  }

}
