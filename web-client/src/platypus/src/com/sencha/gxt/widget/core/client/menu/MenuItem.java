/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.Layer;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.HasIcon;
import com.sencha.gxt.widget.core.client.event.XEvent;

/**
 * A base class for all menu items that require menu-related functionality (like
 * sub-menus) and are not static display items. Item extends the base
 * functionality of {@link Item} by adding menu-specific activation and click
 * handling.
 */
public class MenuItem extends Item implements HasSafeHtml, HasHTML, HasIcon {

  public interface MenuItemAppearance {

    void onAddSubMenu(XElement parent);

    void onRemoveSubMenu(XElement parent);

    void render(SafeHtmlBuilder result);

    void setIcon(XElement parent, ImageResource icon);

    void setText(XElement parent, String text, boolean asHtml);

    void setWidget(XElement parent, Widget widget);

  }

  protected ImageResource icon;
  protected Menu subMenu;
  protected String text;
  protected Widget widget;

  private final MenuItemAppearance appearance;

  /**
   * Creates a new item.
   */
  public MenuItem() {
    this(GWT.<MenuItemAppearance> create(MenuItemAppearance.class));
  }

  /**
   * Creates a menu item with the given appearance.
   * 
   * @param appearance the menu item appearance
   */
  public MenuItem(MenuItemAppearance appearance) {
    this(appearance, GWT.<ItemAppearance> create(ItemAppearance.class));
  }

  /**
   * Creates a menu item with the given appearances.
   * 
   * @param menuItemAppearance the menu item appearance
   * @param itemAppearance the underlying base item appearance
   */
  public MenuItem(MenuItemAppearance menuItemAppearance, ItemAppearance itemAppearance) {
    super(itemAppearance);
    this.appearance = menuItemAppearance;

    canActivate = true;

    SafeHtmlBuilder markupBuilder = new SafeHtmlBuilder();
    appearance.render(markupBuilder);

    setElement(XDOM.create(markupBuilder.toSafeHtml()));
  }

  /**
   * Creates a new item with the given text.
   * 
   * @param text the item's text
   */
  public MenuItem(String text) {
    this();
    setText((text != null && text.equals("&#160;")) ? "" : text);
  }

  /**
   * Creates a new item.
   * 
   * @param text the item's text
   * @param icon the item's icon
   */
  public MenuItem(String text, ImageResource icon) {
    this(text);
    setIcon(icon);
  }

  /**
   * Creates a new item.
   * 
   * @param text the item text
   * @param handler the selection handler
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public MenuItem(String text, SelectionHandler<MenuItem> handler) {
    this(text);
    addSelectionHandler((SelectionHandler) handler);
  }

  /**
   * Expands the item's sub menu.
   */
  public void expandMenu() {
    if (isEnabled() && subMenu != null) {
      subMenu.setFocusOnShow(true);
      subMenu.show(getElement(), new AnchorAlignment(Anchor.TOP_LEFT, Anchor.TOP_RIGHT, true));
    }
  }

  @Override
  public String getHTML() {
    return text;
  }

  /**
   * Returns the item's icon style.
   * 
   * @return the icon style
   */
  public ImageResource getIcon() {
    return icon;
  }

  /**
   * Returns the item's sub menu.
   * 
   * @return the sub menu
   */
  public Menu getSubMenu() {
    return subMenu;
  }

  /**
   * Returns the item's text.
   * 
   * @return the text
   */
  public String getText() {
    return text;
  }

  @Override
  public void setHTML(SafeHtml html) {
    setHTML(html.asString());
  }

  @Override
  public void setHTML(String html) {
    this.text = html;
    appearance.setText(getElement(), html, true);
  }

  @Override
  public void setIcon(ImageResource icon) {
    appearance.setIcon(getElement(), icon);
    this.icon = icon;
  }

  /**
   * Sets the item's sub menu.
   * 
   * @param menu the sub menu
   */
  @UiChild(limit = 1, tagname = "submenu")
  public void setSubMenu(Menu menu) {
    this.subMenu = menu;

    if (menu == null) {
      appearance.onRemoveSubMenu(getElement());
      Accessibility.setState(getElement(), "aria-haspopup", "false");
    } else {
      menu.parentItem = this;
      appearance.onAddSubMenu(getElement());
      Accessibility.setState(getElement(), "aria-haspopup", "true");
    }
  }

  /**
   * Sets the item's text.
   * 
   * @param text the text
   */
  public void setText(String text) {
    this.text = text;
    appearance.setText(getElement(), text, false);
  }

  public void setWidget(Widget widget) {
    this.widget = widget;
    appearance.setWidget(getElement(), widget);
    if (isAttached()) {
      ComponentHelper.doAttach(widget);
    }
  }

  @Override
  protected void activate(boolean autoExpand) {
    super.activate(autoExpand);
    if (autoExpand && subMenu != null) {
      expandMenu();
    }
  }

  @Override
  protected void deactivate() {
    super.deactivate();
    if (subMenu != null && subMenu.isVisible()) {
      subMenu.hide();
    }
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(widget);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(widget);
  }

  @Override
  protected void expandMenu(boolean autoActivate) {
    if (!disabled && subMenu != null) {
      if (!subMenu.isVisible()) {
        expandMenu();
        subMenu.tryActivate(0, 1);
      }
    }
  }

  @Override
  protected boolean shouldDeactivate(NativeEvent ce) {
    if (super.shouldDeactivate(ce)) {
      if (subMenu != null && subMenu.isVisible()) {
        Point xy = ce.<XEvent> cast().getXY();
        xy.setX(xy.getX() + XDOM.getBodyScrollLeft());
        xy.setY(xy.getY() + XDOM.getBodyScrollTop());

        Rectangle rec = subMenu.getElement().getBounds();
        if (getLayer(subMenu) != null) {
          Layer l = getLayer(subMenu);
          if (l.isShim() && l.isShadow()) {
            return !rec.contains(xy) && !l.getShadow().getBounds().contains(xy)
                && !l.getShim().getBounds().contains(xy);
          } else if (l.isShadow()) {
            return !rec.contains(xy) && !l.getShadow().getBounds().contains(xy);
          } else if (l.isShim()) {
            return !rec.contains(xy) && !l.getShim().getBounds().contains(xy);
          }
        }

        return !rec.contains(xy);
      }
    }
    return true;
  }

  private native Layer getLayer(Component c) /*-{
		c.@com.sencha.gxt.widget.core.client.Component::layer;
  }-*/;

}
