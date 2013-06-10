/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.geotools.feature.NameImpl;
import org.geotools.filter.FunctionFactory;
import org.geotools.filter.capability.FunctionNameImpl;
import org.opengis.feature.type.Name;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;

/**
 *
 * @author AB
 */
public class ArrowFunctionFactory implements FunctionFactory{
    
    @Override
    public List<FunctionName> getFunctionNames() {
        List<FunctionName> functionList = new ArrayList<>();
        functionList.add(FunctionEndArrowAngle.NAME);        
        return Collections.unmodifiableList( functionList );
    }    
    @Override
    public org.opengis.filter.expression.Function function(String name, List<Expression> args, Literal fallback) {
        return function(new NameImpl(name), args, fallback);
    }
    @Override
    public org.opengis.filter.expression.Function function(Name name, List<Expression> args, Literal fallback) {
        if(name.getLocalPart().equals(((FunctionNameImpl)FunctionEndArrowAngle.NAME).getFunctionName().getLocalPart())){
            FunctionEndArrowAngle func = new FunctionEndArrowAngle();
            func.setParameters(args);
            func.setFallbackValue(fallback);
            return func;
        }
        return null; // we do not implement that function
    }
}
