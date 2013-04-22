/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package com.eas.designer.application.query.lexer;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author Andrei Badea
 */
public enum LexSqlTokenId implements TokenId {

    // XXX prefix with "sql-" for compat reasons?
    WHITESPACE("whitespace"), // NOI18N
    LINE_COMMENT("linecomment"), // NOI18N
    BLOCK_COMMENT("multilinecomment"), // NOI18N
    STRING("string"), // NOI18N
    INCOMPLETE_STRING("errors"), // NOI18N
    IDENTIFIER("identifier"), // NOI18N
    OPERATOR("operator"), // NOI18N
    LPAREN("operator"), // NOI18N
    RPAREN("operator"), // NOI18N
    DOT("special"), // NOI18N
    COMMA("special"), //  // NOI18N XXX or have own category?
    INT_LITERAL("number"), // NOI18N
    DOUBLE_LITERAL("number"), // NOI18N
    KEYWORD("keyword"); // NOI18N
    private final String primaryCategory;

    private LexSqlTokenId(String aCategory) {
        primaryCategory = aCategory;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory;
    }
    private static final Language<LexSqlTokenId> language = new LanguageHierarchy<LexSqlTokenId>() {
        public static final String PLATYPUS_SQL_MIME_TYPE_NAME = "text/x-platypus-sql";

        @Override
        protected String mimeType() {
            return PLATYPUS_SQL_MIME_TYPE_NAME; // NOI18N
        }

        @Override
        protected Collection<LexSqlTokenId> createTokenIds() {
            return EnumSet.allOf(LexSqlTokenId.class);
        }

        @Override
        protected Map<String, Collection<LexSqlTokenId>> createTokenCategories() {
            return Collections.emptyMap();
        }

        @Override
        protected Lexer<LexSqlTokenId> createLexer(LexerRestartInfo<LexSqlTokenId> info) {
            return new LexSqlLexer(info);
        }

        @Override
        protected LanguageEmbedding<?> embedding(
                Token<LexSqlTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
            return null;
        }
    }.language();

    public static Language<LexSqlTokenId> language() {
        return language;
    }
}
