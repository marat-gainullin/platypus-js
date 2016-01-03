/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.runtime.Source;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class CommentsMinerTest {

    @Test
    public void regExpLiteralWithoutQuotes() {
        String text = ""
                + "/**"
                + " *"
                + " */"
                + "/\\d/";
        Source source = Source.sourceFor("regExpLiteral", text);
        BaseAnnotationsMiner miner = new BaseAnnotationsMiner(source) {
            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
            }
        };
        assertEquals(1, miner.prevComments.size());
    }

    @Test
    public void regExpLiteralWithQuotes() {
        String text = ""
                + "/**"
                + " *"
                + " */"
                + "var r = /\\d\"/;";
        Source source = Source.sourceFor("regExpLiteral", text);
        BaseAnnotationsMiner miner = new BaseAnnotationsMiner(source) {
            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
            }
        };
        assertEquals(1, miner.prevComments.size());
    }
}
