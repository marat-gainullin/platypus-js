/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import java.io.IOException;
import java.io.Writer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLWriter;

/**
 *
 * @author mg
 */
public class HtmlContentEditorKit extends HTMLEditorKit {

    public HtmlContentEditorKit() {
        super();
    }

    @Override
    public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException {

        if (doc instanceof HTMLDocument) {
            HTMLWriter w = new HTMLWriter(out, (HTMLDocument) doc, pos, len) {
                protected boolean bodyStarted;

                @Override
                protected void startTag(Element elem) throws IOException, BadLocationException {
                    if (bodyStarted) {
                        super.startTag(elem);
                    }
                    if (HTML.Tag.BODY == elem.getAttributes().getAttribute(StyleConstants.NameAttribute)) {
                        bodyStarted = true;
                    }
                }

                @Override
                protected void endTag(Element elem) throws IOException {
                    if (HTML.Tag.BODY == elem.getAttributes().getAttribute(StyleConstants.NameAttribute)) {
                        bodyStarted = false;
                    }
                    if (bodyStarted) {
                        super.endTag(elem);
                    }
                }
            };
            w.write();
        } else {
            super.write(out, doc, pos, len);
        }
    }
}
