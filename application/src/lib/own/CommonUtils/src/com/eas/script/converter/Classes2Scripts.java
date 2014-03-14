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
 * The utility to convert JavaScript API classes with @ScritpFunction annotation to pure JavaScript objects.
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
    
    private static final String CONSTRUCTOR_TITLE_TAG = "{$ConstructorTitle}";//NOI18N
    private static final String NAME_TAG = "{$Name}";//NOI18N
    private static final String PARAMS_TAG = "{$Params}";//NOI18N
    private static final String VARS_TAG = "{$Vars}";//NOI18N
    private static final String METHODS_TAG = "{$Methods}";//NOI18N
    private static final String BODY_TAG = "{$Body}";//NOI18N
    private static final String DESCRIPTOR_TAG = "{$Descriptor}";//NOI18N
    private static final String JSDOC_TAG = "{$JsDoc}";//NOI18N
    
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
                    processClass(jarEntry.getName(), child);
                }
            }
        }
    }

    private void processClass(String className, ClassLoader cl) throws ClassNotFoundException, IOException {
        Class clazz = Class.forName(entryName2ClassName(className), true, cl);
        String name = getConstructorName(clazz);
        String js = CONSTRUCTOR_TEMPLATE
                .replace(CONSTRUCTOR_TITLE_TAG, getConstructorTitle(clazz))
                .replace(VARS_TAG, getVarsPart(clazz))
                .replace(METHODS_TAG, getMethodsPart(clazz));
        File resultFile = new File(destDirectory, name + ".js"); //NOI18N
        FileUtils.writeString(resultFile, js, SettingsConstants.COMMON_ENCODING);
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

    private String getConstructorTitle(Class clazz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String getConstructorName(Class clazz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String getVarsPart(Class clazz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private String getPropertyPart(PropBox property) {
        return PROPERTY_TEMPLATE
                .replace(JSDOC_TAG, "")
                .replace(NAME_TAG, property.name)
                .replace(DESCRIPTOR_TAG, "");
    }

    private String getMethodPart(Method method) {
        return METHOD_TEMPLATE
                .replace(JSDOC_TAG, "")
                .replace(NAME_TAG, method.getName())
                .replace(PARAMS_TAG, "")
                .replace(BODY_TAG, "");
    }
    
    private String getMethodsPart(Class clazz) {
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
}
