/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.filter;

import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author kl
 */
public class ScriptTransformerTest {

    @Test
    public void testIfStatement() {
        String va1 = "var closeOnPass = false;\nif (closeOnPass) \n{var t = false;}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".closeOnPass = false;\nif (" + ScriptTransformer.SELF_NAME + ".closeOnPass) \n{\n  " + ScriptTransformer.SELF_NAME + ".t = false;\n}\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testAssignment() {
        String va1 = "var i=Math.sin(0), j; j=2*i;";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".i = Math.sin(0);\n" + ScriptTransformer.SELF_NAME + ".j = null;\n" + ScriptTransformer.SELF_NAME + ".j = 2 * " + ScriptTransformer.SELF_NAME + ".i;\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testCatchClause() {
        String va1 = "var e; try {console.add(1);} catch(ex) {console.log(ex.message); e=ex.message;}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".e = null;\ntry {\n  console.add(1);\n}catch (ex) {\n  console.log(ex.message);\n  " + ScriptTransformer.SELF_NAME + ".e = ex.message;\n}\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testConditionalExpression1() {
        String va1 = "var b, i = null; (i == 'bar') ? b = i : b = 'foo';";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".b = null;\n" + ScriptTransformer.SELF_NAME + ".i = null;\n(" + ScriptTransformer.SELF_NAME + ".i == 'bar') ? " + ScriptTransformer.SELF_NAME + ".b = " + ScriptTransformer.SELF_NAME + ".i : " + ScriptTransformer.SELF_NAME + ".b = 'foo';\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testConditionalExpression2() {
        String va1 = "var b, i = null, i1 = null; b = i ? i : i1";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".b = null;\n" + ScriptTransformer.SELF_NAME + ".i = null;\n" + ScriptTransformer.SELF_NAME + ".i1 = null;\n" + ScriptTransformer.SELF_NAME + ".b = " + ScriptTransformer.SELF_NAME + ".i ? " + ScriptTransformer.SELF_NAME + ".i : " + ScriptTransformer.SELF_NAME + ".i1;\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testContinueStatement() {
        String va1 = "var j=0; for(var i=0; i<10; i++) {if (i<5) continue;j++;break;}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".j = 0;\nfor (" + ScriptTransformer.SELF_NAME + ".i = 0; " + ScriptTransformer.SELF_NAME + ".i < 10; " + ScriptTransformer.SELF_NAME + ".i++) \n  {\n    if (" + ScriptTransformer.SELF_NAME + ".i < 5) \n    continue;\n    " + ScriptTransformer.SELF_NAME + ".j++;\n    break;\n  }\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testDoLoop() {
        String va1 = "var i=0;do{i += 1;}while(i < 5)";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".i = 0;\ndo {\n  " + ScriptTransformer.SELF_NAME + ".i += 1;\n} while (" + ScriptTransformer.SELF_NAME + ".i < 5);\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testElementGet1() {
        String va1 = "var myObj = {foo: 'foo', bar: 'bar'}; var key = 'foo'; var i = myObj[key];";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".myObj = {foo: 'foo', bar: 'bar'};\n" + ScriptTransformer.SELF_NAME + ".key = 'foo';\n" + ScriptTransformer.SELF_NAME + ".i = " + ScriptTransformer.SELF_NAME + ".myObj[" + ScriptTransformer.SELF_NAME + ".key];\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testElementGet2() {
        String va1 = "var myObj = {foo: 'foo', bar: 'bar', foo1: 'foo1'}; var key = 'foo'; var i = myObj[key+Math.cos(0)];";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".myObj = {foo: 'foo', bar: 'bar', foo1: 'foo1'};\n" + ScriptTransformer.SELF_NAME + ".key = 'foo';\n" + ScriptTransformer.SELF_NAME + ".i = " + ScriptTransformer.SELF_NAME + ".myObj[" + ScriptTransformer.SELF_NAME + ".key + Math.cos(0)];\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testElementGet3() {
        String va1 = "var i=1; var myObj = {foo: 'foo', bar: 'bar', foo1: 'foo1'}; var key = 'foo'; var i = myObj[key+i];";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".i = 1;\n" + ScriptTransformer.SELF_NAME + ".myObj = {foo: 'foo', bar: 'bar', foo1: 'foo1'};\n" + ScriptTransformer.SELF_NAME + ".key = 'foo';\n" + ScriptTransformer.SELF_NAME + ".i = " + ScriptTransformer.SELF_NAME + ".myObj[" + ScriptTransformer.SELF_NAME + ".key + " + ScriptTransformer.SELF_NAME + ".i];\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testFunctionCall1() {
        String va1 = "function anyFunction(aParam, bParam){var i; i = aParam; return i;} anyFunction(true, 1);";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".anyFunction = function (aParam, bParam) {\n  var i;\n  i = aParam;\n  return i;\n};\n" + ScriptTransformer.SELF_NAME + ".anyFunction(true, 1);\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testFunctionCall2() {
        String va1 = "var a=1; function anyFunction(aParam, bParam){var i; i = aParam; return i;} anyFunction(true, a);";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".a = 1;\n" + ScriptTransformer.SELF_NAME + ".anyFunction = function (aParam, bParam) {\n  var i;\n  i = aParam;\n  return i;\n};\n" + ScriptTransformer.SELF_NAME + ".anyFunction(true, " + ScriptTransformer.SELF_NAME + ".a);\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testFunctionCall3() {
        String va1 = "window.close();";
        String va2 = "window.close();\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testFunctionCall4() {
        String va1 = "var k=2;var m=(function(){for(var i=0; i<5; i++){if(i==k){return i;}}})();";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".k = 2;\n" + ScriptTransformer.SELF_NAME + ".m = (function() {\n  for (var i = 0; i < 5; i++) \n    {\n      if (i == " + ScriptTransformer.SELF_NAME + ".k) \n      {\n        return i;\n      }\n    }\n})();\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testFunctionCall5() {
        String va1 = "var key='key';function getKey(){return key;}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".key = 'key';\n" + ScriptTransformer.SELF_NAME + ".getKey = function () {\n  return " + ScriptTransformer.SELF_NAME + ".key;\n};\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testIfStatement1() {
        String va1 = "var a, b;if(a==1){var b = a;} else {b = null;}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".a = null;\n" + ScriptTransformer.SELF_NAME + ".b = null;\nif (" + ScriptTransformer.SELF_NAME + ".a == 1) \n{\n  " + ScriptTransformer.SELF_NAME + ".b = " + ScriptTransformer.SELF_NAME + ".a;\n} else {\n  " + ScriptTransformer.SELF_NAME + ".b = null;\n}\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testInfixExpression() {
        String va1 = "var a; a < 10;";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".a = null;\n" + ScriptTransformer.SELF_NAME + ".a < 10;\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testSwitchCase1() {
        String va1 = "var d = 'none';\nvar s = 'name';\nvar k;\nswitch(d){\ncase s: k = null;\nbreak; case k:\ns = Math.round(0);}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".d = 'none';\n" + ScriptTransformer.SELF_NAME + ".s = 'name';\n" + ScriptTransformer.SELF_NAME + ".k = null;\nswitch (" + ScriptTransformer.SELF_NAME + ".d) {\n  case " + ScriptTransformer.SELF_NAME + ".s:\n    " + ScriptTransformer.SELF_NAME + ".k = null;\n    break;\n  case " + ScriptTransformer.SELF_NAME + ".k:\n    " + ScriptTransformer.SELF_NAME + ".s = Math.round(0);\n}\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testParenthesizedExpression() {
        String va1 = "var d = 'none';\nvar s = 'name';\nvar k;\nswitch(d){\ncase (s): k = null;\nbreak; case (k):\ns = Math.round(0);}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".d = 'none';\n" + ScriptTransformer.SELF_NAME + ".s = 'name';\n" + ScriptTransformer.SELF_NAME + ".k = null;\nswitch (" + ScriptTransformer.SELF_NAME + ".d) {\n  case (" + ScriptTransformer.SELF_NAME + ".s):\n    " + ScriptTransformer.SELF_NAME + ".k = null;\n    break;\n  case (" + ScriptTransformer.SELF_NAME + ".k):\n    " + ScriptTransformer.SELF_NAME + ".s = Math.round(0);\n}\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }
    
    @Test
    public void testSwitchCase3() {
        String va1 = "var d = 'none'; var s = 'name'; var k; switch(s = d.toLowerCase()){case d.toLowerCase(): k = null; break; case d + s:k = Math.round(0);}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".d = 'none';\n" + ScriptTransformer.SELF_NAME + ".s = 'name';\n" + ScriptTransformer.SELF_NAME + ".k = null;\nswitch (" + ScriptTransformer.SELF_NAME + ".s = " + ScriptTransformer.SELF_NAME + ".d.toLowerCase()) {\n  case " + ScriptTransformer.SELF_NAME + ".d.toLowerCase():\n    " + ScriptTransformer.SELF_NAME + ".k = null;\n    break;\n  case " + ScriptTransformer.SELF_NAME + ".d + " + ScriptTransformer.SELF_NAME + ".s:\n    " + ScriptTransformer.SELF_NAME + ".k = Math.round(0);\n}\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testTryStatement() {
        String va1 = "var m = 1;try {m++;} catch(ex) {console.log(m.toFixed(2));} finally {m++;}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".m = 1;\ntry {\n  " + ScriptTransformer.SELF_NAME + ".m++;\n}catch (ex) {\n  console.log(" + ScriptTransformer.SELF_NAME + ".m.toFixed(2));\n}\n finally {\n  " + ScriptTransformer.SELF_NAME + ".m++;\n}\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testWhileLoop() {
        String va1 = "var m = 0, k = 3;while(k < 10){m++;}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".m = 0;\n" + ScriptTransformer.SELF_NAME + ".k = 3;\nwhile (" + ScriptTransformer.SELF_NAME + ".k < 10) \n  {\n    " + ScriptTransformer.SELF_NAME + ".m++;\n  }\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testWithStatement() {
        String va1 = "with({num: 1, min: 0, max: 10}){console.log(num);}";
        String va2 = "with ({num: 1, min: 0, max: 10})   {\n    console.log(num);\n  }\n;\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testVariableInitializer() {
        String va1 = "var prop = null;";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".prop = null;\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testVariableDefinition() {
        String va1 = "var v1 = \"abc\", v2 = 333, v3 = 2 * v2;";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".v1 = \"abc\";\n" + ScriptTransformer.SELF_NAME + ".v2 = 333;\n" + ScriptTransformer.SELF_NAME + ".v3 = 2 * v2;\n";
        String va3 = "" + ScriptTransformer.SELF_NAME + ".v1 = \"abc\";\n" + ScriptTransformer.SELF_NAME + ".v2 = 333;\n" + ScriptTransformer.SELF_NAME + ".v3 = 2 * " + ScriptTransformer.SELF_NAME + ".v2;\n";
        String va4 = transform(va1);
        assertNotSame(va4, va2);
        assertEquals(va4, va3);
    }

    @Test
    public void testSimpleFunctionNode() {
        String va1 = "function someFunc(){}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".someFunc = function () {\n};\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testHardFunctionNode() {
        String va1 = "var v1 = 1;\nfunction someFunction(){var v2 = \"abs\", v3 = 1;\nv3++;\nreturn v1*2*v3;}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".v1 = 1;\n" + ScriptTransformer.SELF_NAME + ".someFunction = function () {\n  var v2 = \"abs\", v3 = 1;\n  v3++;\n  return " + ScriptTransformer.SELF_NAME + ".v1 * 2 * v3;\n};\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testArrayLiteral() {
        String va1 = "var v4 = null, arr = [1, 3, 5, 7, v4];";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".v4 = null;\n" + ScriptTransformer.SELF_NAME + ".arr = [1, 3, 5, 7, " + ScriptTransformer.SELF_NAME + ".v4];\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testObjectLiteral() {
        String va1 = "var v4 = null, obj = {p1: 1, v4: 2, p3: 3, p4: v4};";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".v4 = null;\n" + ScriptTransformer.SELF_NAME + ".obj = {p1: 1, v4: 2, p3: 3, p4: " + ScriptTransformer.SELF_NAME + ".v4};\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testArrayComprehension() {
        String va1 = "var i; var arr = [0, 1, i]; var s = [2*i for (i in arr) if (i*i>3)];";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".i = null;\n" + ScriptTransformer.SELF_NAME + ".arr = [0, 1, " + ScriptTransformer.SELF_NAME + ".i];\n" + ScriptTransformer.SELF_NAME + ".s = [2 * " + ScriptTransformer.SELF_NAME + ".i for (i in " + ScriptTransformer.SELF_NAME + ".arr) if (" + ScriptTransformer.SELF_NAME + ".i * " + ScriptTransformer.SELF_NAME + ".i > 3)];\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testForLoop1() {
        String va1 = "var j=0;\nfor(var i=0; i<10; i++){j++}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".j = 0;\nfor (" + ScriptTransformer.SELF_NAME + ".i = 0; " + ScriptTransformer.SELF_NAME + ".i < 10; " + ScriptTransformer.SELF_NAME + ".i++) \n  {\n    " + ScriptTransformer.SELF_NAME + ".j++;\n  }\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testForLoop2() {
        String va1 = "var j=0, i;\nfor(j=0; j<10; j++){i++}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".j = 0;\n" + ScriptTransformer.SELF_NAME + ".i = null;\nfor (" + ScriptTransformer.SELF_NAME + ".j = 0; " + ScriptTransformer.SELF_NAME + ".j < 10; " + ScriptTransformer.SELF_NAME + ".j++) \n  {\n    " + ScriptTransformer.SELF_NAME + ".i++;\n  }\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testForLoop3() {
        String va1 = "for(var a=1, b=2, c=b; a<b; a++){}";
        String va2 = "for (" + ScriptTransformer.SELF_NAME + ".a = 1 , " + ScriptTransformer.SELF_NAME + ".b = 2 , " + ScriptTransformer.SELF_NAME + ".c = " + ScriptTransformer.SELF_NAME + ".b; " + ScriptTransformer.SELF_NAME + ".a < " + ScriptTransformer.SELF_NAME + ".b; " + ScriptTransformer.SELF_NAME + ".a++) \n  {\n  }\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testForInLoop1() {
        String va1 = "var j, a; var arr = [0, j]; for(i in arr){a++}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".j = null;\n" + ScriptTransformer.SELF_NAME + ".a = null;\n" + ScriptTransformer.SELF_NAME + ".arr = [0, " + ScriptTransformer.SELF_NAME + ".j];\nfor (i in " + ScriptTransformer.SELF_NAME + ".arr) \n  {\n    " + ScriptTransformer.SELF_NAME + ".a++;\n  }\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testForInLoop2() {
        String va1 = "var j, i; var arr = [0, j]; for(i in arr){a++}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".j = null;\n" + ScriptTransformer.SELF_NAME + ".i = null;\n" + ScriptTransformer.SELF_NAME + ".arr = [0, " + ScriptTransformer.SELF_NAME + ".j];\nfor (" + ScriptTransformer.SELF_NAME + ".i in " + ScriptTransformer.SELF_NAME + ".arr) \n  {\n    a++;\n  }\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testForInLoop3() {
        String va1 = "var j, a; var arr = [0, j]; for(var i in arr){a++}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".j = null;\n" + ScriptTransformer.SELF_NAME + ".a = null;\n" + ScriptTransformer.SELF_NAME + ".arr = [0, " + ScriptTransformer.SELF_NAME + ".j];\n" + ScriptTransformer.SELF_NAME + ".i = null;\nfor (" + ScriptTransformer.SELF_NAME + ".i in " + ScriptTransformer.SELF_NAME + ".arr) \n  {\n    " + ScriptTransformer.SELF_NAME + ".a++;\n  }\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testSideEffect() {
        String va1 = "var j=0, i;\nfor(j=0; j<10; j++){i++}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".j = 0;\n" + ScriptTransformer.SELF_NAME + ".i = null;\nfor (" + ScriptTransformer.SELF_NAME + ".j = 0; " + ScriptTransformer.SELF_NAME + ".j < 10; " + ScriptTransformer.SELF_NAME + ".j++) \n  {\n    " + ScriptTransformer.SELF_NAME + ".i++;\n  }\n";
        String va3 = transform(va1);
        String va4 = transform(va3);
        assertEquals(va3, va2);
        assertEquals(va3, va4);
    }

    @Test
    public void testInternalScopeConst1() {
        String va1 = "function func(){\nconst m = 3;\nvar n = 4;\nreturn m;\n}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".func = function () {\n  var m = 3;\n  var n = 4;\n  return m;\n};\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testInternalScopeConst2() {
        String va1 = "function f1(){const m1 = 1;function f2(){const m2 = 2;return m2;}return m1;}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".f1 = function () {\n  var m1 = 1;\n  function f2() {\n    var m2 = 2;\n    return m2;\n  }\n  return m1;\n};\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testClassDifinition() {
        String va1 = "function myClass(){" + ScriptTransformer.SELF_NAME + ".prop1 = null;" + ScriptTransformer.SELF_NAME + ".getPropOne = function(){return " + ScriptTransformer.SELF_NAME + ".prop1;}" + ScriptTransformer.SELF_NAME + ".setPropOne = function(aProp){" + ScriptTransformer.SELF_NAME + ".prop1 = aProp;};}var cls = new myClass(); console.log(cls.getPropOne());";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".myClass = function () {\n  " + ScriptTransformer.SELF_NAME + ".prop1 = null;\n  " + ScriptTransformer.SELF_NAME + ".getPropOne = function() {\n  return " + ScriptTransformer.SELF_NAME + ".prop1;\n};\n  " + ScriptTransformer.SELF_NAME + ".setPropOne = function(aProp) {\n  " + ScriptTransformer.SELF_NAME + ".prop1 = aProp;\n};\n};\n" + ScriptTransformer.SELF_NAME + ".cls = new " + ScriptTransformer.SELF_NAME + ".myClass();\nconsole.log(" + ScriptTransformer.SELF_NAME + ".cls.getPropOne());\n";
        String va3 = transform(va1);
        assertEquals(va3, va2);
    }

    @Test
    public void testParseDependencies1() {
        String va1 = "var moduleName = 'SOME_MODULE_NAME'; Modules.get(moduleName);";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".moduleName = 'SOME_MODULE_NAME';\nModules.get(" + ScriptTransformer.SELF_NAME + ".moduleName);\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        String va3 = transformer.transform();
        assertEquals(va3, va2);
        assertTrue(transformer.getDependencies().isEmpty());
    }

    @Test
    public void testParseDependencies2() {
        String SOME_MODULE_TWO = "SOME_MODULE_TWO";
        String va1 = "var moduleName = 'SOME_MODULE_NAME'; Modules.get(\"" + SOME_MODULE_TWO + "\");";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".moduleName = 'SOME_MODULE_NAME';\nModules.get(\"" + SOME_MODULE_TWO + "\");\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        String va3 = transformer.transform();
        assertEquals(va3, va2);
        Set<String> dependencies = transformer.getDependencies();
        assertTrue(dependencies.size() == 1);
        assertTrue(dependencies.contains(SOME_MODULE_TWO));
    }

    @Test
    public void testParseDependencies3() {
        String FORM_ID_HERE = "FORM_ID_HERE";
        String va1 = "var form = new Form(\"" + FORM_ID_HERE + "\");";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".form = new " + FORM_ID_HERE + "();\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        String va3 = transformer.transform();
        assertEquals(va3, va2);
        Set<String> dependencies = transformer.getDependencies();
        assertTrue(dependencies.size() == 1);
        assertTrue(dependencies.contains(FORM_ID_HERE));
    }

    @Test
    public void testParseDependencies4() {
        String va1 = "var report = new Report(\"ANY_REPORT_NAME\"); var module = new Module(\"ANY_MODULE_NAME\"); var form = new Form(\"123456789123456\");";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".report = new ServerReportANY_REPORT_NAME();\n" + ScriptTransformer.SELF_NAME + ".module = new ANY_MODULE_NAME();\n" + ScriptTransformer.SELF_NAME + ".form = new Form123456789123456();\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        String va3 = transformer.transform();
        assertEquals(va3, va2);
        assertFalse(transformer.getDependencies().isEmpty());
        assertEquals(2, transformer.getDependencies().size());
        assertEquals(1, transformer.getServerDependencies().size());
    }

    @Test
    public void testParseDependencies5() {
        String va1 = "var report = new ServerReport(\"ANY_REPORT_NAME\"); var module = new Module(\"ANY_MODULE_NAME\"); var report2 = new Report(\"ANY_REPORT_NAME\"); var servModule = new ServerModule(\"ANY_MODULE_NAME\");";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".report = new ServerReportANY_REPORT_NAME();\n" + ScriptTransformer.SELF_NAME + ".module = new ANY_MODULE_NAME();\n" + ScriptTransformer.SELF_NAME + ".report2 = new ServerReportANY_REPORT_NAME();\n" + ScriptTransformer.SELF_NAME + ".servModule = new ServerModuleANY_MODULE_NAME();\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        String va3 = transformer.transform();
        assertEquals(va3, va2);
        assertFalse(transformer.getDependencies().isEmpty());
        assertEquals(1, transformer.getDependencies().size());
        assertEquals(2, transformer.getServerDependencies().size());
    }

    @Test
    public void testParseDependencies6() {
        String va1 = "require([\"ANY_REPORT_NAME\", \"ANY_MODULE_NAME\"], function(){var report = new ServerReport(\"ANY_REPORT_NAME\"); var module = new Module(\"ANY_MODULE1_NAME\"); var report2 = new Report(\"ANY_REPORT_NAME\"); var servModule = new ServerModule(\"ANY_MODULE_NAME\");});";
        String va2 = "require([\"ANY_REPORT_NAME\", \"ANY_MODULE_NAME\"], function() {\n  var report = new ServerReportANY_REPORT_NAME();\n  var module = new ANY_MODULE1_NAME();\n  var report2 = new ServerReportANY_REPORT_NAME();\n  var servModule = new ServerModuleANY_MODULE_NAME();\n});\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        String va3 = transformer.transform();
        assertEquals(va3, va2);
        assertFalse(transformer.getDependencies().isEmpty());
        assertEquals(1, transformer.getDependencies().size());
        assertTrue(transformer.getServerDependencies().isEmpty());
    }

    @Test
    public void testParseDependencies7() {
        String va1 = "require([\"ANY_REPORT_NAME\", \"ANY_MODULE_NAME\", \"ANY_MODULE1_NAME\"], function(){var report = new ServerReport(\"ANY_REPORT_NAME\"); var module = new Module(\"ANY_MODULE1_NAME\"); var report2 = new Report(\"ANY_REPORT_NAME\"); var servModule = new ServerModule(\"ANY_MODULE_NAME\");});";
        String va2 = "require([\"ANY_REPORT_NAME\", \"ANY_MODULE_NAME\", \"ANY_MODULE1_NAME\"], function() {\n  var report = new ServerReportANY_REPORT_NAME();\n  var module = new ANY_MODULE1_NAME();\n  var report2 = new ServerReportANY_REPORT_NAME();\n  var servModule = new ServerModuleANY_MODULE_NAME();\n});\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        String va3 = transformer.transform();
        assertEquals(va3, va2);
        assertTrue(transformer.getDependencies().isEmpty());
        assertTrue(transformer.getServerDependencies().isEmpty());
    }

    @Test
    public void testParseDependencies8() {
        String va1 = "require(\"ANY_REPORT_NAME\", function(){var report = new ServerReport(\"ANY_REPORT_NAME\"); var report2 = new Report(\"ANY_REPORT_NAME\")});";
        String va2 = "require(\"ANY_REPORT_NAME\", function() {\n  var report = new ServerReportANY_REPORT_NAME();\n  var report2 = new ServerReportANY_REPORT_NAME();\n});\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        String va3 = transformer.transform();
        assertEquals(va3, va2);
        assertTrue(transformer.getDependencies().isEmpty());
        assertTrue(transformer.getServerDependencies().isEmpty());
    }
    
    @Test
    public void testParseDependencies9() {
        String va1 = "var q = model.loadEntity('someQuery');";
        String va2 = "_platypusModuleSelf.q = _platypusModuleSelf.model.loadEntity('someQuery');\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        transformer.addExternalVariable("model");
        String va3 = transformer.transform();
        assertEquals(va2, va3);
        assertTrue(transformer.getDependencies().isEmpty());
        assertTrue(transformer.getServerDependencies().isEmpty());
        assertEquals(1, transformer.getQueryDependencies().size());
        assertEquals("someQuery", transformer.getQueryDependencies().iterator().next());
    }
    
    @Test
    public void testExternalVariable() {
        String va1 = "model.save();";
        String va2 = "model.save();\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        String va3 = transformer.transform();
        assertEquals(va3, va2);
        transformer.addExternalVariable("model");
        String va4 = transformer.transform();
        String va5 = "" + ScriptTransformer.SELF_NAME + ".model.save();\n";
        assertEquals(va4, va5);
        assertNotSame(va3, va4);
    }

    @Test
    public void testExternalVariable2() {
        String va1 = "var p1 = Param1;";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".p1 = " + ScriptTransformer.SELF_NAME + ".Param1;\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        transformer.addExternalVariable("Param1");
        String va3 = transformer.transform();
        assertEquals(va3, va2);
    }

    @Test
    public void testTopLevelInsidePropertyGet() {
        String va1 = ""
                + "var p1 = params.Param1;\n"
                + "var p1 = Param1;";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".p1 = " + ScriptTransformer.SELF_NAME + ".params.Param1;\n" + ScriptTransformer.SELF_NAME + ".p1 = " + ScriptTransformer.SELF_NAME + ".Param1;\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        transformer.addExternalVariable("params");
        transformer.addExternalVariable("Param1");
        String va3 = transformer.transform();
        assertEquals(va3, va2);
    }

    @Test
    public void testTopLevelInsideFunction() {
        String va1 = "function test(){\n"
                + "var p1 = params.Param1;\n"
                + "var p1 = Param1;\n"
                + "}";
        String va2 = "" + ScriptTransformer.SELF_NAME + ".test = function () {\n"
                + "  var p1 = " + ScriptTransformer.SELF_NAME + ".params.Param1;\n"
                + "  var p1 = " + ScriptTransformer.SELF_NAME + ".Param1;\n"
                + "};\n";
        ScriptTransformer transformer = new ScriptTransformer(va1);
        transformer.addExternalVariable("params");
        transformer.addExternalVariable("Param1");
        String va3 = transformer.transform();
        assertEquals(va3, va2);
    }

    private static String transform(String aSource) {
        ScriptTransformer transformer = new ScriptTransformer(aSource);
        return transformer.transform();
    }
}
