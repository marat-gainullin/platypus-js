/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.utils.syntax;

/**
 *
 * @author Марат
 */
public class SqlEditorKit extends SyntaxEditorKit{

    public SqlEditorKit()
    {
        super();
    }

    @Override
    public String getContentType() {
        return JSyntaxPane.CONTENT_TYPE_SQL;
    }

    @Override
    public void createHighlighting() {
        highlighting = new SqlHighlighter();
    }

}
