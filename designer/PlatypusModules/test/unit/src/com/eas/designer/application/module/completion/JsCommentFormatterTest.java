/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vv
 */
public class JsCommentFormatterTest {
    
    public static final String JS_DOC = "/**\n"
            + "* Creates a new Circle from a diameter.\n"
            + "*\n"
            + "* @param {number} d The desired diameter of the circle.\n"
            + "* @return {Circle} The new Circle object.\n"
            + "*/\n";
    public static final String HTML ="<b>Summary</b><blockquote>Creates a new Circle from a diameter.</blockquote>"
            + "<b>Parameters</b><blockquote><i>{number}</i> <b>d</b> The desired diameter of the circle.</blockquote>"
            + "<b>Returns</b><blockquote>{Circle} The new Circle object.</blockquote>";
                 
    public JsCommentFormatterTest() {
    }

    @Test
    public void testProcess() {
        List<String> comments = JsCompletionSupport.getComments(JS_DOC);
        JsCommentFormatter formatter = new JsCommentFormatter(comments);
        String html = formatter.toHtml();
        assertEquals(html, HTML);
    }
}
