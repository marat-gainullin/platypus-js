/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.lexer;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

/**
 *
 * @author mg
 */
public class SqlTokenId implements TokenId {

    private static final Language<SqlTokenId> language = new SqlLanguageHierarchy().language();

    public static Language<SqlTokenId> getLanguage() {
        return language;
    }
    private final String name;
    private final String primaryCategory;
    private final int id;

    SqlTokenId(
            String aName,
            String aCategory,
            int aId) {
        name = aName;
        primaryCategory = aCategory;
        id = aId;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory;
    }

    @Override
    public int ordinal() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }
}
