package com.eas.client.utils.syntax;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Марат
 */
public class JsEditorKit extends SyntaxEditorKit{

    public JsEditorKit()
    {
        super();
    }

    @Override
    public String getContentType() {
        return JSyntaxPane.CONTENT_TYPE_JS;
    }

    @Override
    public void createHighlighting() {
        highlighting = new JsHighlighting();
    }
}
