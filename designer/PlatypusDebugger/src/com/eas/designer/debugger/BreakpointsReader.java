/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import java.net.MalformedURLException;
import java.net.URL;
import org.netbeans.api.debugger.Properties;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;

/**
 *
 * @author mg
 */
public class BreakpointsReader implements Properties.Reader {

    @Override
    public String[] getSupportedClassNames() {
        return new String[]{PlatypusBreakpoint.class.getName()};
    }

    @Override
    public Object read(String typeID, Properties properties) {
        if (typeID.equals(PlatypusBreakpoint.class.getName())) {
            Line line = getLine(
                    properties.getString("url", null),
                    properties.getInt("lineNumber", 1));
            if (line != null) {
                PlatypusBreakpoint breakpoint = new PlatypusBreakpoint(line);
                breakpoint.disable();
                if (properties.getBoolean(PlatypusBreakpoint.PROP_ENABLED, true)) {
                    breakpoint.enable();
                } else {
                    breakpoint.disable();
                }
                return breakpoint;
            }
        }
        return null;
    }

    @Override
    public void write(Object object, Properties properties) {
        if (object instanceof PlatypusBreakpoint) {
            PlatypusBreakpoint b = (PlatypusBreakpoint) object;
            FileObject fo = (FileObject) b.getLine().getLookup().lookup(FileObject.class);
            properties.setString("url", fo.toURL().toString());
            properties.setInt(
                    "lineNumber",
                    b.getLine().getLineNumber());
            properties.setBoolean(PlatypusBreakpoint.PROP_ENABLED, ((PlatypusBreakpoint) object).isEnabled());
        }
    }

    private Line getLine(String url, int lineNumber) {
        FileObject file;
        try {
            file = URLMapper.findFileObject(new URL(url));
        } catch (MalformedURLException e) {
            return null;
        }
        if (file == null) {
            return null;
        }
        DataObject dataObject;
        try {
            dataObject = DataObject.find(file);
        } catch (DataObjectNotFoundException ex) {
            return null;
        }
        if (dataObject == null) {
            return null;
        }
        LineCookie lineCookie = dataObject.getLookup().lookup(LineCookie.class);
        if (lineCookie == null) {
            return null;
        }
        Line.Set ls = lineCookie.getLineSet();
        if (ls == null) {
            return null;
        }
        try {
            return ls.getCurrent(lineNumber);
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
        }
        return null;
    }
}
