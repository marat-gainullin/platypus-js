/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * A layout container that lays out its children using an HTML template. The
 * mapping of each child to a corresponding selector is specified using
 * <code>HtmlData</code>.
 * <p/>
 * Code Snippet #1 - The first code snippet is a simple example that uses
 * in-line HTML:
 * 
 * <pre>
  public interface HtmlLayoutContainerTemplate extends XTemplates {
    {@code @XTemplate("<div><div class='cell1'></div><div class='cell2'></div><div class='cell3'></div></div>")}
    SafeHtml getTemplate();
  }

  private void onModuleLoad() {
    HtmlLayoutContainerTemplate templates = GWT.create(HtmlLayoutContainerTemplate.class);
    HtmlLayoutContainer c = new HtmlLayoutContainer(templates.getTemplate());
    c.add(new TextButton("Button 1"), new HtmlData(".cell1"));
    c.add(new TextButton("Button 2"), new HtmlData(".cell2"));
    c.add(new TextButton("Button 3"), new HtmlData(".cell3"));
    Viewport v = new Viewport();
    v.add(c);
    RootPanel.get().add(v);
  }
 * </pre>
 * <p/>
 * Code Snippet #2 - The second code snippet is more complex and illustrates the
 * use of external HTML and CSS files:
 * <ul>
 * <li>CodeSnippet.html - the HTML template</li>
 * <li>CodeSnippet.css - the CSS for the template</li>
 * </ul>
 * The external HTML and CSS are injected into the content that is downloaded to
 * the browser.
 * 
 * <pre>
  public interface CodeSnippetStyle extends CssResource {
    String cell1();
    String cell2();
    String cell3();
  }

  public interface CodeSnippetHtml extends XTemplates {
    {@code @}XTemplate(source = "CodeSnippet.html")
    SafeHtml getTemplate(CodeSnippetStyle style);
  }

  public interface CodeSnippetCss extends ClientBundle {
    {@code @}Source("CodeSnippet.css")
    CodeSnippetStyle style();
  }

  private void onModuleLoad() {
    CodeSnippetHtml html = GWT.create(CodeSnippetHtml.class);
    CodeSnippetCss css = GWT.create(CodeSnippetCss.class);
    css.style().ensureInjected();
    HtmlLayoutContainer c = new HtmlLayoutContainer(html.getTemplate(css.style()));
    c.add(new Label("Label 1"), new HtmlData("." + css.style().cell1()));
    c.add(new Label("Label 2"), new HtmlData("." + css.style().cell2()));
    c.add(new Label("Label 3"), new HtmlData("." + css.style().cell3()));
    Viewport v = new Viewport();
    v.add(c);
    RootPanel.get().add(v);
  }
 * </pre>
 * By default, GWT generates short, obfuscated names for the injected CSS. For
 * debugging purposes, instruct GWT to generate recognizable style names by
 * including the following in the module file (.gwt.xml).
 * 
 * <pre>
 * {@code <set-configuration-property name='CssResource.style' value='pretty' />}
 * </pre>
 * 
 * CodeSnippet.html contains the HTML, for example:
 * 
 * <pre>
&lt;div&gt;
  &lt;div class="{style.cell1}"&gt;&lt;/div&gt;
  &lt;div class="{style.cell2}"&gt;&lt;/div&gt;
  &lt;div class="{style.cell3}"&gt;&lt;/div&gt;
&lt;/div&gt;
 * </pre>
 * 
 * CodeSnippet.css contains the styles, for example:
 * 
 * <pre>
.cell1 {
  background-color: #ffffcc;
}
.cell2 {
  background-color: #ccffff;
}
.cell3 {
  background-color: #ffccff;
}
 * </pre>
 */
public class HtmlLayoutContainer extends AbstractHtmlLayoutContainer {

  /**
   * Creates an HTML layout container using the specified HTML template.
   * 
   * @param html the HTML representation of the layout container
   */
  public HtmlLayoutContainer(SafeHtml html) {
    super();
    setHTML(html);
  }

  /**
   * Creates an HTML layout container using the specified HTML template.
   * 
   * @param html the HTML representation of the layout container
   */
  public HtmlLayoutContainer(String html) {
    super();
    setHTML(html);
  }

  @Override
  public SafeHtml getHTML() {
    return super.getHTML();
  }

  @Override
  public void setHTML(SafeHtml html) {
    super.setHTML(html);
  }

  /**
   * Sets the HTML template for the layout container.
   * 
   * @param html the HTML template for the layout container
   */
  public void setHTML(String html) {
    setHTML(SafeHtmlUtils.fromSafeConstant(html));
  }

}
