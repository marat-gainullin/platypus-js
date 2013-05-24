/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.TextBox;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent;
import com.sencha.gxt.data.shared.loader.LoaderHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * A specialized toolbar that is bound to a {@link PagingLoader} and provides
 * automatic paging controls.
 * 
 * <p />
 * The tool bar is "bound" to the loader using the {@link #bind(PagingLoader)}
 * method.
 */
public class PagingToolBar extends ToolBar {

  public interface PagingToolBarAppearance {

    ImageResource first();

    ImageResource last();

    ImageResource loading();

    ImageResource next();

    ImageResource prev();

    ImageResource refresh();
  }

  public interface PagingToolBarMessages {
    String afterPageText(int page);

    String beforePageText();

    String displayMessage(int start, int end, int total);

    String emptyMessage();

    String firstText();

    String lastText();

    String nextText();

    String prevText();

    String refreshText();
  }

  protected static class DefaultPagingToolBarMessages implements PagingToolBarMessages {

    @Override
    public String afterPageText(int page) {
      return DefaultMessages.getMessages().pagingToolBar_afterPageText(page);
    }

    @Override
    public String beforePageText() {
      return DefaultMessages.getMessages().pagingToolBar_beforePageText();
    }

    @Override
    public String displayMessage(int start, int end, int total) {
      return DefaultMessages.getMessages().pagingToolBar_displayMsg(start, end, total);
    }

    @Override
    public String emptyMessage() {
      return DefaultMessages.getMessages().pagingToolBar_emptyMsg();
    }

    @Override
    public String firstText() {
      return DefaultMessages.getMessages().pagingToolBar_firstText();
    }

    @Override
    public String lastText() {
      return DefaultMessages.getMessages().pagingToolBar_lastText();
    }

    @Override
    public String nextText() {
      return DefaultMessages.getMessages().pagingToolBar_nextText();
    }

    @Override
    public String prevText() {
      return DefaultMessages.getMessages().pagingToolBar_prevText();
    }

    @Override
    public String refreshText() {
      return DefaultMessages.getMessages().pagingToolBar_refreshText();
    }

  }

  protected int activePage = -1, pages;
  protected LabelToolItem beforePage, afterText, displayText;
  protected PagingLoadConfig config;
  protected TextButton first, prev, next, last, refresh;
  protected PagingLoader<PagingLoadConfig, ?> loader;
  protected TextBox pageText;

  protected boolean savedEnableState = true;
  protected boolean showToolTips = true;
  protected int start, pageSize, totalLength;
  private final PagingToolBarAppearance appearance;
  private boolean loading;

  private LoaderHandler<PagingLoadConfig, ?> handler = new LoaderHandler<PagingLoadConfig, PagingLoadResult<?>>() {

    @Override
    public void onBeforeLoad(final BeforeLoadEvent<PagingLoadConfig> event) {
      loading = true;
      savedEnableState = isEnabled();
      setEnabled(false);
      refresh.setIcon(appearance.loading());
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

        @Override
        public void execute() {
          if (event.isCancelled()) {
            refresh.setIcon(appearance.refresh());
            setEnabled(savedEnableState);
          }
        }
      });
    }

    @Override
    public void onLoad(LoadEvent<PagingLoadConfig, PagingLoadResult<?>> event) {
      refresh.setIcon(appearance.refresh());
      setEnabled(savedEnableState);
      PagingToolBar.this.onLoad(event);
      loading = false;
    }

    @Override
    public void onLoadException(LoadExceptionEvent<PagingLoadConfig> event) {
      refresh.setIcon(appearance.refresh());
      setEnabled(savedEnableState);
      loading = false;
    }
  };

  private HandlerRegistration handlerRegistration;
  private PagingToolBarMessages messages;
  private boolean reuseConfig = true;
  
  /**
   * Creates a new paging tool bar.
   * 
   * @param pageSize the page size
   */
  @UiConstructor
  public PagingToolBar(int pageSize) {
    this(GWT.<ToolBarAppearance> create(ToolBarAppearance.class),
        GWT.<PagingToolBarAppearance> create(PagingToolBarAppearance.class), pageSize);
  }

  /**
   * Creates a new tool bar.
   * 
   * @param toolBarAppearance the tool bar appearance
   * @param appearance the paging tool bar appearance
   * @param pageSize the page size
   */
  public PagingToolBar(ToolBarAppearance toolBarAppearance, PagingToolBarAppearance appearance, int pageSize) {
    super(toolBarAppearance);
    this.appearance = appearance;
    this.pageSize = pageSize;

    first = new TextButton();
    first.setIcon(appearance.first());
    first.addSelectHandler(new SelectHandler() {

      @Override
      public void onSelect(SelectEvent event) {
        first();
      }
    });

    prev = new TextButton();
    prev.setIcon(appearance.prev());
    prev.addSelectHandler(new SelectHandler() {

      @Override
      public void onSelect(SelectEvent event) {
        previous();
      }
    });

    next = new TextButton();
    next.setIcon(appearance.next());
    next.addSelectHandler(new SelectHandler() {

      @Override
      public void onSelect(SelectEvent event) {
        next();
      }
    });

    last = new TextButton();
    last.setIcon(appearance.last());
    last.addSelectHandler(new SelectHandler() {

      @Override
      public void onSelect(SelectEvent event) {
        last();
      }
    });

    refresh = new TextButton();
    refresh.setIcon(appearance.refresh());
    refresh.addSelectHandler(new SelectHandler() {

      @Override
      public void onSelect(SelectEvent event) {
        refresh();
      }
    });

    beforePage = new LabelToolItem();
    beforePage.setLabel(getMessages().beforePageText());

    afterText = new LabelToolItem();

    pageText = new TextBox();
    pageText.setWidth("30px");
    pageText.addKeyDownHandler(new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          onPageChange();
        }
      }
    });

    displayText = new LabelToolItem();
    displayText.addStyleName(CommonStyles.get().nowrap());

    add(first);
    add(prev);
    add(new SeparatorToolItem());
    add(beforePage);
    add(pageText);
    add(afterText);
    add(new SeparatorToolItem());
    add(next);
    add(last);
    add(new SeparatorToolItem());
    add(refresh);
    add(new FillToolItem());
    add(displayText);
  }

  /**
   * Binds the toolbar to the loader.
   * 
   * @param loader the loader
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void bind(PagingLoader<? extends PagingLoadConfig, ?> loader) {
    if (this.loader != null) {
      handlerRegistration.removeHandler();
    }
    this.loader = (PagingLoader) loader;
    if (loader != null) {
      loader.setLimit(pageSize);
      // the loader and the handler have the same generics, the cast is required
      // because neither one cares about the data in the load result. Unsure of
      // a better way to express this.
      handlerRegistration = loader.addLoaderHandler((LoaderHandler) handler);
    }
  }

  /**
   * Clears the current toolbar text.
   */
  public void clear() {
    pageText.setText("");
    afterText.setLabel("");
    displayText.setLabel("");
  }

  /**
   * Moves to the first page.
   */
  public void first() {
    if (!loading) {
      doLoadRequest(0, pageSize);
    }
  }

  /**
   * Returns the active page.
   * 
   * @return the active page
   */
  public int getActivePage() {
    return activePage;
  }

  /**
   * Returns the toolbar appearance.
   * 
   * @return the appearance
   */
  public PagingToolBarAppearance getAppearance() {
    return appearance;
  }

  /**
   * Returns the toolbar messages.
   * 
   * @return the messages
   */
  public PagingToolBarMessages getMessages() {
    if (messages == null) {
      messages = new DefaultPagingToolBarMessages();
    }
    return messages;
  }

  /**
   * Returns the current page size.
   * 
   * @return the page size
   */
  public int getPageSize() {
    return pageSize;
  }

  /**
   * Returns the total number of pages.
   * 
   * @return the
   */
  public int getTotalPages() {
    return pages;
  }

  /**
   * Returns true if the previous load config is reused.
   * 
   * @return the reuse config state
   */
  public boolean isReuseConfig() {
    return reuseConfig;
  }

  /**
   * Returns true if tooltip are enabled.
   * 
   * @return the show tooltip state
   */
  public boolean isShowToolTips() {
    return showToolTips;
  }

  /**
   * Moves to the last page.
   */
  public void last() {
    if (!loading) {
      if (totalLength > 0) {
        int extra = totalLength % pageSize;
        int lastStart = extra > 0 ? (totalLength - extra) : totalLength - pageSize;
        doLoadRequest(lastStart, pageSize);
      }
    }
  }

  /**
   * Moves to the last page.
   */
  public void next() {
    if (!loading) {
      doLoadRequest(start + pageSize, pageSize);
    }
  }

  /**
   * Moves the the previous page.
   */
  public void previous() {
    if (!loading) {
      doLoadRequest(Math.max(0, start - pageSize), pageSize);
    }
  }

  /**
   * Refreshes the data using the current configuration.
   */
  public void refresh() {
    if (!loading) {
      doLoadRequest(start, pageSize);
    }
  }

  /**
   * Sets the active page (1 to page count inclusive).
   * 
   * @param page the page
   */
  public void setActivePage(int page) {
    if (page > pages) {
      last();
      return;
    }
    if (page != activePage && page > 0 && page <= pages) {
      doLoadRequest(--page * pageSize, pageSize);
    } else {
      pageText.setText(String.valueOf((int) activePage));
    }
  }

  /**
   * Sets the toolbar messages.
   * 
   * @param messages the messages
   */
  public void setMessages(PagingToolBarMessages messages) {
    this.messages = messages;
  }

  /**
   * Sets the current page size. This method does not effect the data currently
   * being displayed. The new page size will not be used until the next load
   * request.
   * 
   * @param pageSize the new page size
   */
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * True to reuse the previous load config (defaults to true).
   * 
   * @param reuseConfig true to reuse the load config
   */
  public void setReuseConfig(boolean reuseConfig) {
    this.reuseConfig = reuseConfig;
  }

  /**
   * Sets if the button tool tips should be displayed (defaults to true,
   * pre-render).
   * 
   * @param showToolTips true to show tool tips
   */
  public void setShowToolTips(boolean showToolTips) {
    this.showToolTips = showToolTips;
  }

  protected void doLoadRequest(int offset, int limit) {
    if (reuseConfig && config != null) {
      config.setOffset(offset);
      config.setLimit(pageSize);
      loader.load(config);
    } else {
      loader.setLimit(pageSize);
      loader.load(offset, limit);
    }
  }

  protected void onLoad(LoadEvent<PagingLoadConfig, PagingLoadResult<?>> event) {
    config = event.getLoadConfig();
    PagingLoadResult<?> result = event.getLoadResult();
    start = result.getOffset();
    totalLength = result.getTotalLength();
    activePage = (int) Math.ceil((double) (start + pageSize) / pageSize);

    pages = totalLength < pageSize ? 1 : (int) Math.ceil((double) totalLength / pageSize);

    if (activePage > pages && totalLength > 0) {
      last();
      return;
    } else if (activePage > pages) {
      start = 0;
      activePage = 1;
    }

    pageText.setText(String.valueOf((int) activePage));

    String after = null, display = null;
    after = getMessages().afterPageText(pages);
    afterText.setLabel(after);

    first.setEnabled(activePage != 1);
    prev.setEnabled(activePage != 1);
    next.setEnabled(activePage != pages);
    last.setEnabled(activePage != pages);

    int temp = activePage == pages ? totalLength : start + pageSize;

    display = getMessages().displayMessage(start + 1, (int) temp, totalLength);

    String msg = display;
    if (totalLength == 0) {
      msg = getMessages().emptyMessage();
    }
    displayText.setLabel(msg);

    forceLayout();
  }

  protected void onPageChange() {
    String value = pageText.getText();
    if (value.equals("") || !Util.isInteger(value)) {
      pageText.setText(String.valueOf((int) activePage));
      return;
    }
    int p = Integer.parseInt(value);
    setActivePage(p);
  }

}
