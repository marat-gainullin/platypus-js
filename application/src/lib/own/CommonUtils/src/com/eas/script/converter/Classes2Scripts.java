/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script.converter;

import com.eas.client.settings.SettingsConstants;
import com.eas.script.ScriptFunction;
import com.eas.util.FileUtils;
import com.eas.util.PropertiesUtils;
import com.eas.util.PropertiesUtils.PropBox;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The utility to convert JavaScript API classes with @ScritpFunction annotation
 * to pure JavaScript objects.
 *
 * @author vv
 */
public class Classes2Scripts {

    public static final String CMD_SWITCHS_PREFIX = "-";//NOI18N
    public static final String CLASS_PATH_CMD_SWITCH = "cp";//NOI18N
    public static final String DESTINATION_DIRECTORY_CMD_SWITCH = "dest";//NOI18N

    private static final String JAVA_CLASS_FILE_EXT = ".class";//NOI18N
    private static final String CONSTRUCTOR_TEMPLATE = getStringResource("constructorTemplate.js");//NOI18N
    private static final String PROPERTY_TEMPLATE = getStringResource("propertyTemplate.js");//NOI18N
    private static final String METHOD_TEMPLATE = getStringResource("methodTemplate.js");//NOI18N

    private static final String NAME_TAG = "{$Name}";//NOI18N
    private static final String PARAMS_TAG = "{$Params}";//NOI18N
    private static final String VARS_TAG = "{$Vars}";//NOI18N
    private static final String METHODS_TAG = "{$Methods}";//NOI18N
    private static final String BODY_TAG = "{$Body}";//NOI18N
    private static final String DESCRIPTOR_TAG = "{$Descriptor}";//NOI18N
    private static final String JSDOC_TAG = "{$JsDoc}";//NOI18N
    private static final String DELELGATE_CLASS = "__JavaClass";//NOI18N
    private static final String DELELGATE_OBJECT = "__javaObj";//NOI18N
    private static final String DEFAULT_CONSTRUCTOR_JS_DOC = "/**\n"//NOI18N
            + "* Generated constructor.\n"//NOI18N
            + "*/";//NOI18N

    private static Classes2Scripts convertor;
    private final List<File> classPaths = new ArrayList<>();
    private File destDirectory;

    public static void main(String[] args) {
        convertor = new Classes2Scripts();
        try {
            convertor.parseArguments(args);
            convertor.validate();
            convertor.run();
            System.out.println("Conversion completed.");
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            Logger.getLogger(Classes2Scripts.class.getName()).log(Level.WARNING, null, ex);
        }

    }

    private void parseArguments(String[] args) throws Exception {
        int i = 0;
        while (i < args.length) {
            if ((CMD_SWITCHS_PREFIX + CLASS_PATH_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    i += 1;
                    while (i < args.length - 1 && !args[i].startsWith(CMD_SWITCHS_PREFIX)) {
                        classPaths.add(new File(args[i]));
                        i++;
                    }
                } else {
                    throw new IllegalArgumentException("Source directory argument syntax error");
                }
            } else if ((CMD_SWITCHS_PREFIX + DESTINATION_DIRECTORY_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    destDirectory = new File(args[i + 1]);
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Layout directory argument syntax error");
                }
            } else {
                throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }
        }
    }

    private void validate() {
        if (!destDirectory.isDirectory()) {
            throw new IllegalArgumentException("Destination path is not a directory: " + destDirectory);
        }

        for (File classPath : classPaths) {
            if (!classPath.isDirectory() && !isJar(classPath)) {
                throw new IllegalArgumentException("Class path is not a directory nor a jar file: " + classPath);
            }
        }
    }

    private static boolean isJar(File f) {
        return f.isFile() && f.getName().endsWith("jar");//NOI18N
    }

    private void run() {
        try {
            for (File classPath : classPaths) {
                if (classPath.isDirectory()) {
                    processDirectory(classPath);
                } else {
                    processJar(classPath);
                }
            }
        } catch (IOException | ClassNotFoundException ex) {

        }
    }

    private void processDirectory(File classPath) throws IOException, ClassNotFoundException {
        for (File f : classPath.listFiles()) {
            if (f.isDirectory()) {
                processDirectory(f);
            } else if (isJar(f)) {
                processJar(f);
            }
        }
    }

    private void processJar(File file) throws IOException, ClassNotFoundException {
        try (JarFile jar = new JarFile(file)) {
            URLClassLoader child = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
            Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                if (jarEntry.getName().endsWith(JAVA_CLASS_FILE_EXT)) {
                    Class clazz = Class.forName(entryName2ClassName(jarEntry.getName()), true, child);
                    File resultFile = new File(destDirectory, getJsConstructorInfo(clazz).name + ".js"); //NOI18N
                    FileUtils.writeString(resultFile, getClassJs(clazz), SettingsConstants.COMMON_ENCODING);
                }
            }
        }
    }

    protected String getClassJs(Class clazz) {
        FunctionInfo ci = getJsConstructorInfo(clazz);
        String js = CONSTRUCTOR_TEMPLATE
                .replace(JSDOC_TAG, ci.jsDoc)
                .replace(NAME_TAG, ci.name)
                .replace(PARAMS_TAG, ci.params)
                .replace(VARS_TAG, getVarsPart(clazz))
                .replace(METHODS_TAG, getPropsAndMethodsPart(clazz));
        return js;
    }

    private static String entryName2ClassName(String entryName) {
        return entryName.substring(entryName.length() - JAVA_CLASS_FILE_EXT.length(), entryName.length()).replace("/", ".");//NOI18N
    }

    private static String getStringResource(String resName) {
        try {
            return FileUtils.readString(new File(Classes2Scripts.class.getResource(resName).toURI()), SettingsConstants.COMMON_ENCODING);//NOI18N
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    private FunctionInfo getJsConstructorInfo(Class clazz) {
        FunctionInfo ci = new FunctionInfo();
        for (Constructor constr : clazz.getConstructors()) {
            if (constr.isAnnotationPresent(ScriptFunction.class)) {
                return getFunctionInfo(constr);
            }
        }
        ci.jsDoc = DEFAULT_CONSTRUCTOR_JS_DOC;
        ci.name = clazz.getSimpleName();
        ci.params = "";//NOI18N
        return ci;
    }

    private FunctionInfo getFunctionInfo(AnnotatedElement ae) {
        FunctionInfo ci = new FunctionInfo();
        ScriptFunction sf = (ScriptFunction) ae.getAnnotation(ScriptFunction.class);
        ci.name = sf.name();
        ci.jsDoc = sf.jsDoc();
        StringBuilder paramsSb = new StringBuilder();
        for (int i = 0; i < sf.params().length; i++) {
            paramsSb.append(sf.params()[i]);
            if (i < sf.params().length - 1) {
                paramsSb.append(", ");//NOI18N
            }
        }
        ci.params = paramsSb.toString();
        return ci;
    }

    private String getVarsPart(Class clazz) {
        return String.format("var %s = Java.type(\"%s\");\n", DELELGATE_CLASS, clazz.getName())//NOI18N
                + String.format("var %s = new %s;", DELELGATE_OBJECT, DELELGATE_CLASS);//NOI18N
    }

    private String getPropertyPart(PropBox property) {
        return PROPERTY_TEMPLATE
                .replace(JSDOC_TAG, property.jsDoc)
                .replace(NAME_TAG, property.name)
                .replace(DESCRIPTOR_TAG, getPropertyDescriptor(property));
    }

    private String getPropertyDescriptor(PropBox property) {
        StringBuilder sb = new StringBuilder();
        sb.append("\twritable: ").append(Boolean.toString(property.writeable)).append(",\n");//NOI18N
        
        sb.append("\tget: ").append(getPropertyGetFunction(property));
        
        if (property.writeable) {
            sb.append(",\n");
            sb.append("\tset: ").append(getPropertySetFunction(property));
        }
        return sb.toString();
    }

    private String getPropertyGetFunction(PropBox property) {
        return String.format("function() { return %s() }", property.readMethodName); 
    }
    
    private String getPropertySetFunction(PropBox property) {
        return String.format("function(val) { %s(val) }", property.writeMethodName); 
    }
    
    private String getMethodPart(Method method) {
        FunctionInfo fi = getFunctionInfo(method);
        return METHOD_TEMPLATE
                .replace(JSDOC_TAG, fi.jsDoc)
                .replace(NAME_TAG, fi.name)
                .replace(PARAMS_TAG, fi.params)
                .replace(BODY_TAG, getMethodBody(method.getName(), fi.params, !Void.TYPE.equals(method.getReturnType())));
    }

    private String getMethodBody(String methodName, String methodParams, boolean returnsValue) {
        if (returnsValue) {
            return String.format("\treturn %s(%s);", methodName, methodParams);//NOI18N
        } else {
            return String.format("\t%s(%s);", methodName, methodParams);//NOI18N
        }
    }

    private String getPropsAndMethodsPart(Class clazz) {
        Map<String, PropertiesUtils.PropBox> props = new HashMap<>();
        List<Method> methods = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(ScriptFunction.class)) {
                if (PropertiesUtils.isBeanPatternMethod(method)) {
                    String propName = PropertiesUtils.getPropertyName(method.getName());
                    PropertiesUtils.PropBox pb = props.get(propName);
                    if (pb == null) {
                        pb = new PropertiesUtils.PropBox();
                        pb.name = propName;
                        props.put(pb.name, pb);
                    }
                    PropertiesUtils.setPropertyAccessStatus(pb, method.getName());
                    PropertiesUtils.setPropertyReturnType(pb, method);
                    if (pb.jsDoc == null || pb.jsDoc.isEmpty()) {
                        pb.jsDoc = method.getAnnotation(ScriptFunction.class).jsDoc();
                    }
                } else {
                    methods.add(method);
                }
            }
        }
        for (PropBox property : props.values()) {
            sb.append(getPropertyPart(property));
            sb.append("\n");//NOI18N
        }
        for (Method method : methods) {
            sb.append(getMethodPart(method));
            sb.append("\n");//NOI18N
        }
        return sb.toString();
    }

    protected static class FunctionInfo {

        public String name;
        public String params;
        public String jsDoc;
    }
}
