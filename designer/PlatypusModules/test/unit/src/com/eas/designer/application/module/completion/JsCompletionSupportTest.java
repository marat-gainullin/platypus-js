package com.eas.designer.application.module.completion;

import com.eas.designer.application.module.completion.JsCompletionProvider.CompletionPoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.loaders.DataObjectExistsException;

/**
 *
 * @author vv
 */
public class JsCompletionSupportTest {
        
    private static final String JS = "  function f(param1, param2) \n"//30
            + "{ alert(v); function f2(arg) { alert(arg);  alert(arg); alert(arg); } }\n"//80 
            + "function f3(myParam) {var j = 1; alert(j); }\n" //116
            + " var v='text i';\n" //150
            + "v = 'new text';\n"
            + "result = f3(v);\n";
    
    private static final String JS_DOC = "/**\n"
            + "* Creates a new Circle from a diameter.\n"
            + "*\n"
            + "* @param {number} d The desired diameter of the circle.\n"
            + "* @return {Circle} The new Circle object.\n"
            + "*/\n";
             
    private static final String JS_DOC_FUNCTION = "function f4(d) {\n"
             + "}\n";
    
    private Set<JsFunction> functionResultsSet;
    private Set<String> fieldsResultsSet;
    
    public JsCompletionSupportTest() {
    }

    /**
     * Test of parse method, of class JsCompletionSupport.
     */
    @Test
    public void testParse() throws DataObjectExistsException {        
        ModuleCompletionContext.ScanJsItemsSupport instance;
        CompletionPoint point = new CompletionPoint();
        Collection<JsCompletionItem> results;
        Set<JsFunction> functionSet;
        Set<String> fieldsSet;
        // verify top level scope
        point.caretBeginWordOffset = point.caretEndWordOffset = 0; //Caret position
        instance = new ModuleCompletionContext.ScanJsItemsSupport();
        String source = JS + JS_DOC + JS_DOC_FUNCTION;
        DataObjectMock astProvider = new DataObjectMock(source);
        results = instance.getCompletionItems(astProvider, "", point.caretBeginWordOffset, point);
        assertTrue(results != null);
        assertTrue(results.size() > 0);       
        JsFunction[] functions0 = { new JsFunction("f", "param1", "param2"), //NOI18N
                                    new JsFunction("f3", "myParam"), //NOI18N
                                    new JsFunction("f4", "d")}; //NOI18N
        functionSet = new HashSet<> (Arrays.asList(functions0));
        setResults(results);
        printResults();
        assertTrue(functionSet.equals(functionResultsSet));
        String[] fields0 = {"v"}; //NOI18N
        fieldsSet = new HashSet<> (Arrays.asList(fields0));
        assertTrue(fieldsSet.equals(fieldsResultsSet));
        
        //verify embedded level
        point.caretBeginWordOffset = point.caretEndWordOffset = 35; //Caret position
        instance = new ModuleCompletionContext.ScanJsItemsSupport();
        results = instance.getCompletionItems(astProvider, "", point.caretBeginWordOffset, point); //NOI18N
        assertTrue(results != null);
        assertTrue(results.size() > 0);       
        JsFunction[] functions1 = { new JsFunction("f", "param1", "param2"), //NOI18N
                                    new JsFunction("f3", "myParam"),
                                    new JsFunction("f2", "arg"),
                                    new JsFunction("f4", "d")}; //NOI18N
        functionSet = new HashSet<> (Arrays.asList(functions1));
        setResults(results);
        printResults();
        assertTrue(functionSet.equals(functionResultsSet));
        String[] fields1 = {"v", "param1", "param2"}; //NOI18N
        fieldsSet = new HashSet<> (Arrays.asList(fields1));
        assertTrue(fieldsSet.equals(fieldsResultsSet));   
    }
    
    private void printResults() {
        System.out.println("functions: " + functionResultsSet);//NOI18N
        System.out.println("fields: " + fieldsResultsSet);//NOI18N
    }
            
    private void setResults(Collection<JsCompletionItem> results) {
        functionResultsSet = new HashSet<>();
        fieldsResultsSet = new HashSet<>();
        for (JsCompletionItem result : results) {
            if (result instanceof JsFunctionCompletionItem) {
                JsFunctionCompletionItem item = (JsFunctionCompletionItem) result;
                String[] params = item.getParams().toArray(new String[item.getParams().size()]);
                functionResultsSet.add(new JsFunction(item.text, params));
            }
            if (result instanceof JsFieldCompletionItem) {
                JsFieldCompletionItem item = (JsFieldCompletionItem) result;
                fieldsResultsSet.add(item.text);
            }
        }
    }
    
    private static class JsFunction {
        private final String name;
        private final List<String> params;

        public String getName() {
            return name;
        }

        public List<String> getParams() {
            return Collections.unmodifiableList(params);
        }
        
        public JsFunction(String name, String... params) {
            this.name = name;
            this.params = new ArrayList<>(Arrays.asList(params));
        }

        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + Objects.hashCode(this.name);
            hash = 89 * hash + Objects.hashCode(this.params);
            return hash;
        }
        
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof JsFunction)) {
                return false;
            }
            JsFunction that = (JsFunction)o;
            return name.equals(that.name) && this.getParams().equals(that.getParams());
        }
        
        public String toString() {
            return name + "(" + getParamsHtmlText() + ")"; //NOI18N
        }
        
        private String getParamsHtmlText() {
        if (params == null || params.isEmpty()) {
            return ""; //NOI18N
        }
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<params.size(); i++) {
           sb.append(params.get(i));
            if (i < params.size() - 1) {
                sb.append(", ");  //NOI18N
            }
        }
        return sb.toString();
    }
    }
}
