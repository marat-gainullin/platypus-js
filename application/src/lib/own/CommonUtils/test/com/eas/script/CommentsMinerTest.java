/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import java.io.IOException;
import java.net.URL;
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
        AnnotatedNodeVisitor miner = new AnnotatedNodeVisitor(source) {
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
        Source source = Source.sourceFor("regExpLiteral1", text);
        AnnotatedNodeVisitor miner = new AnnotatedNodeVisitor(source) {
            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
            }
        };
        assertEquals(1, miner.prevComments.size());
    }

    @Test
    public void regExpLiteralWithQuotesComment1() {
        String text = ""
                + "/**"
                + " * first comment"
                + " */"
                + "var r = /* second comment *//\\d\"/;";
        Source source = Source.sourceFor("regExpLiteral2", text);
        AnnotatedNodeVisitor miner = new AnnotatedNodeVisitor(source) {
            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
            }
        };
        assertEquals(2, miner.prevComments.size());
    }

    @Test
    public void regExpLiteralWithQuotesComment2() {
        String text = ""
                + "/**"
                + " * first comment"
                + " */"
                + "var r = /* second comment *//\\d\"//*third comment*/;";
        Source source = Source.sourceFor("regExpLiteral3", text);
        AnnotatedNodeVisitor miner = new AnnotatedNodeVisitor(source) {
            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
            }
        };
        assertEquals(3, miner.prevComments.size());
    }

    @Test
    public void regExpLiteralWithQuotesComment3() {
        String text = ""
                + "/**"
                + " * first comment"
                + " */"
                + "var r = // second comment \n/\\d\"//*third comment*/;";
        Source source = Source.sourceFor("regExpLiteral3", text);
        AnnotatedNodeVisitor miner = new AnnotatedNodeVisitor(source) {
            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
            }
        };
        assertEquals(3, miner.prevComments.size());
    }

    @Test
    public void regExpLiteralWithQuotesAndFlags() {
        String text = ""
                + "/**"
                + " * first comment"
                + " */"
                + "var r = // second comment \n/\\d\"/ig/*third comment*/;";
        Source source = Source.sourceFor("regExpLiteral3", text);
        AnnotatedNodeVisitor miner = new AnnotatedNodeVisitor(source) {
            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
            }
        };
        assertEquals(3, miner.prevComments.size());
    }

    @Test
    public void jQueryLargeFileMinified() throws IOException {
        URL resource = CommentsMinerTest.class.getClassLoader().getResource("com/eas/script/reg-exp-literal.min-jquery.js");
        Source source = Source.sourceFor("jquery-large-min", resource);
        AnnotatedNodeVisitor miner = new AnnotatedNodeVisitor(source) {
            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
            }
        };
        assertEquals(1, miner.prevComments.size());
    }

    @Test
    public void jQueryLargeFile() throws IOException {
        URL resource = CommentsMinerTest.class.getClassLoader().getResource("com/eas/script/reg-exp-literal-jquery.js");
        Source source = Source.sourceFor("leaflet-large", resource);
        AnnotatedNodeVisitor miner = new AnnotatedNodeVisitor(source) {
            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
            }
        };
        assertEquals(1573, miner.prevComments.size());
    }
    
    @Test
    public void leafletLargeFile() throws IOException {
        URL resource = CommentsMinerTest.class.getClassLoader().getResource("com/eas/script/numeric-literal-leaflet.js");
        Source source = Source.sourceFor("jquery-large", resource);
        AnnotatedNodeVisitor miner = new AnnotatedNodeVisitor(source) { 
            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
            }
        };
        assertEquals(1, miner.prevComments.size());
    }
}
