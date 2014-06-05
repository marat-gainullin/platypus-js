/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import com.eas.client.settings.SettingsConstants;
import com.eas.util.FileUtils;
import com.eas.util.PropertiesUtils;
import com.eas.util.PropertiesUtils.PropBox;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The utility application to convert JavaScript API classes with
 *
 * @ScritpFunction annotation to pure JavaScript objects.
 *
 * @author vv
 */
public class Classes2Scripts {

    public static final String CMD_SWITCHS_PREFIX = "-";//NOI18N
    public static final String CLASS_PATH_CMD_SWITCH = "dirs";//NOI18N
    public static final String DESTINATION_DIRECTORY_CMD_SWITCH = "dest";//NOI18N

    private static final String JAVA_CLASS_FILE_EXT = ".class";//NOI18N
    private static final String CONSTRUCTOR_TEMPLATE = getStringResource("constructorTemplate.js");//NOI18N
    private static final String DEPS_FILE_NAME = "deps.js";//NOI18N

    private static final int DEFAULT_IDENTATION_WIDTH = 4;
    private static final int CONSTRUCTOR_IDENT_LEVEL = 1;

    private static final String NAME_TAG = "${Name}";//NOI18N
    private static final String JAVA_TYPE_TAG = "${Type}";//NOI18N
    private static final String PARAMS_TAG = "${Params}";//NOI18N
    private static final String NULL_PARAMS_TAG = "${NullParams}";//NOI18N
    private static final String UNWRAPPED_PARAMS_TAG = "${UnwrappedParams}";//NOI18N
    private static final String MAX_ARGS_TAG = "${MaxArgs}";//NOI18N
    private static final String PROPERTIES_TAG = "${Props}";//NOI18N
    private static final String JSDOC_TAG = "${JsDoc}";//NOI18N
    private static final String DELEGATE_TAG = "${Delegate}";//NOI18N
    private static final String DELEGATE_OBJECT = "delegate";//NOI18N
    private static final String DEFAULT_CONSTRUCTOR_JS_DOC = ""
            + "/**\n"//NOI18N
            + " * Generated constructor.\n"//NOI18N
            + " */";//NOI18N

    private static final String DEFAULT_PROPERTY_JS_DOC = ""
            + "/**\n"//NOI18N
            + " * Generated property jsDoc.\n"//NOI18N
            + " */";//NOI18N

    private static final String DEFAULT_METHOD_JS_DOC = ""
            + "/**\n"//NOI18N
            + " * Generated method jsDoc.\n"//NOI18N
            + " */";//NOI18N

    private static final String JS_DOC_TEMPLATE = ""
            + "/**\n"//NOI18N
            + " * %s\n"//NOI18N
            + " */";//NOI18N

    private static final String DEPS_HEADER = ""
            + "/**\n"//NOI18N
            + " * Contains the basic dependencies loading.\n"//NOI18N
            + " */\n";//NOI18N
    private static Classes2Scripts convertor;
    private final List<File> classPaths = new ArrayList<>();
    private File destDirectory;
    private final Set<String> depsPaths = new TreeSet<>();

    public static void main(String[] args) throws Exception {
        convertor = new Classes2Scripts();
        try {
            convertor.parseArguments(args);
            convertor.validate();
            convertor.clean();
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
        if (!destDirectory.exists()) {
            throw new IllegalArgumentException("Destination directory does not exists: " + destDirectory);
        }

        if (!destDirectory.isDirectory()) {
            throw new IllegalArgumentException("Destination is not a directory: " + destDirectory);
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

    private void clean() throws IOException {
        if (!destDirectory.isDirectory()) {
            throw new IllegalArgumentException("Only directory can be used as dest."); // NOI18N
        }
        for (File c : destDirectory.listFiles()) {
            if (!"platypus.js".equals(c.getName()) && !"internals.js".equals(c.getName())) {
                FileUtils.delete(c);
            }
        }
    }

    private void run() {
        try {
            for (File classPath : classPaths) {
                if (classPath.isDirectory()) {
                    processDirectory(classPath);
                } else if (isJar(classPath)) {
                    processJar(classPath);
                }
            }
            createDepsFile();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Classes2Scripts.class.getName()).log(Level.SEVERE, "Conversion error.", ex);
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

    private void processJar(File jarFile) throws IOException, ClassNotFoundException {
        try (JarFile jar = new JarFile(jarFile)) {
            Logger.getLogger(Classes2Scripts.class.getName())
                    .log(Level.INFO, "Processing jar: {0}", new String[]{jarFile.getAbsolutePath()});
            URLClassLoader cl = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, this.getClass().getClassLoader());
            Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                try {
                    JarEntry jarEntry = e.nextElement();
                    if (jarEntry.getName().endsWith(JAVA_CLASS_FILE_EXT)) {
                        String className = entryName2ClassName(jarEntry.getName());
                        Class clazz = Class.forName(className, false, cl);
                        FunctionInfo jsConstructor = getJsConstructorInfo(clazz);
                        if (jsConstructor != null) {
                            String js = getClassJs(clazz);
                            if (js != null) {
                                File subDir = new File(destDirectory, FileNameSupport.getFileName(FileUtils.removeExtension(jarFile.getName())));
                                if (!subDir.exists()) {
                                    subDir.mkdir();
                                }
                                Logger.getLogger(Classes2Scripts.class.getName())
                                        .log(Level.INFO, "\tClass name: {0}", new String[]{className});
                                File resultFile = new File(subDir, FileNameSupport.getFileName(jsConstructor.name) + ".js"); //NOI18N
                                FileUtils.writeString(resultFile, js, SettingsConstants.COMMON_ENCODING);
                                depsPaths.add(getRelativePath(destDirectory, resultFile));
                            }
                        }
                    }
                } catch (NoClassDefFoundError ex) {
                    //NO-OP
                }
            }
        }
    }

    protected String getClassJs(Class clazz) {
        FunctionInfo ci = getJsConstructorInfo(clazz);
        if (ci.javaClassName.contains("$")) {
            Logger.getLogger(Classes2Scripts.class.getName()).log(Level.WARNING, "======================================= Inner class: {0}", ci.javaClassName);
            return null;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            Logger.getLogger(Classes2Scripts.class.getName()).log(Level.WARNING, "======================================= Abstract class: {0}", ci.javaClassName);
            return null;
        }
        checkForHasPublised(clazz);
        checkForSetPublisher(clazz);
        String js = CONSTRUCTOR_TEMPLATE
                .replace(JAVA_TYPE_TAG, ci.javaClassName)
                .replace(JSDOC_TAG, getConstructorJsDoc(ci))
                .replace(NAME_TAG, ci.name)
                .replace(NULL_PARAMS_TAG, ci.getNullParamsStr())
                .replace(PARAMS_TAG, ci.getParamsStr())
                .replace(DELEGATE_TAG, DELEGATE_OBJECT)
                .replace(UNWRAPPED_PARAMS_TAG, ci.getUnwrappedParamsStr())
                .replace(MAX_ARGS_TAG, Integer.toString(ci.params.length))
                .replace(PROPERTIES_TAG, getPropsAndMethodsPart(clazz, CONSTRUCTOR_IDENT_LEVEL + 1));
        return js;
    }

    private void createDepsFile() throws IOException {
        File deps = new File(destDirectory, DEPS_FILE_NAME);
        FileUtils.writeString(deps, getDepsJsContent(), SettingsConstants.COMMON_ENCODING);
    }

    private String getDepsJsContent() {
        StringBuilder sb = new StringBuilder(DEPS_HEADER);
        if (!depsPaths.isEmpty()) {
            String dir = "";
            String indent = getIndentStr(1);
            sb.append("try {\n");
            for (String path : depsPaths) {
                String pathDir = pathRootDir(path);
                if (!dir.equals(pathDir) && !dir.isEmpty()) {
                    sb.append(indent).append("printf('").append(dir).append(" API loaded.');\n");
                    sb.append("} catch (e) {\n");
                    sb.append(indent).append("printf('").append(dir).append(" API skipped.');\n");
                    sb.append("}\n");
                    sb.append("\n");
                    sb.append("try {\n");
                }
                dir = pathDir;
                sb.append(indent).append(String.format("load('classpath:%s');\n", FileNameSupport.getFileName(path)));
            }
            sb.append(indent).append("printf('").append(dir).append(" API loaded.');\n");
            sb.append("} catch (e) {\n");
            sb.append(indent).append("printf('").append(dir).append(" API skipped.');\n");
            sb.append("}\n");
        }
        return sb.toString();
    }

    private static String pathRootDir(String path) {
        String[] pathElements = path.split("/");
        if (pathElements.length > 0) {
            return pathElements[0];
        } else {
            return null;
        }
    }

    private static String getRelativePath(File base, File file) {
        Path pathAbsolute = file.toPath();
        Path pathBase = base.toPath();
        return pathBase.relativize(pathAbsolute).toString().replace("\\", "/");
    }

    private static void checkForHasPublised(Class clazz) {
        if (!HasPublished.class.isAssignableFrom(clazz)) {
            Logger.getLogger(Classes2Scripts.class.getName()).log(Level.WARNING, "HasPublished iterface is not implemented: {0}", clazz.getName());
        }
    }

    private static void checkForSetPublisher(Class clazz) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals("setPublisher") && Modifier.isStatic(m.getModifiers())) {
                return;
            }
        }
        Logger.getLogger(Classes2Scripts.class.getName()).log(Level.WARNING, "setPublisher static method is not implemented: {0}", clazz.getName());
    }

    private static String getConstructorJsDoc(FunctionInfo ci) {
        return addIndent(appendLine2JsDoc(formJsDoc(ci.jsDoc), "@namespace " + ci.name), CONSTRUCTOR_IDENT_LEVEL);
    }

    private static String getIndentStr(int ident) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DEFAULT_IDENTATION_WIDTH * ident; i++) {
            sb.append(" ");//NOI18N
        }
        return sb.toString();
    }

    private static String entryName2ClassName(String entryName) {
        return entryName.substring(0, entryName.length() - JAVA_CLASS_FILE_EXT.length()).replace("/", ".");//NOI18N
    }

    private static String getStringResource(String resName) {
        try {
            return FileUtils.readString(new File(Classes2Scripts.class.getResource(resName).toURI()), SettingsConstants.COMMON_ENCODING);//NOI18N
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    private FunctionInfo getJsConstructorInfo(Class clazz) {
        try {
            for (Constructor constr : clazz.getConstructors()) {
                if (constr.isAnnotationPresent(ScriptFunction.class)) {
                    return getConstructorInfo(clazz.getName(), clazz.getSimpleName(), constr);
                }
            }
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(ScriptFunction.class)) {
                    return getSimpleConstructorInfo(clazz.getName(), clazz.getSimpleName());
                }
            }
        } catch (Exception ex) {
            //NO-OP
        }
        return null;
    }

    private FunctionInfo getSimpleConstructorInfo(String javaType, String name) {
        FunctionInfo fi = new FunctionInfo();
        fi.javaClassName = javaType;
        fi.name = name;
        fi.jsDoc = DEFAULT_CONSTRUCTOR_JS_DOC;
        return fi;
    }

    private FunctionInfo getConstructorInfo(String javaType, String defaultName, AnnotatedElement ae) {
        FunctionInfo fi = getFunctionInfo(defaultName, ae);
        fi.javaClassName = javaType;
        return fi;
    }

    private FunctionInfo getFunctionInfo(String defaultName, AnnotatedElement ae) {
        FunctionInfo ci = new FunctionInfo();
        ScriptFunction sf = (ScriptFunction) ae.getAnnotation(ScriptFunction.class);
        ci.name = sf.name().isEmpty() ? defaultName : sf.name();
        ci.jsDoc = formJsDoc(sf.jsDoc());
        ci.params = new String[sf.params().length];
        System.arraycopy(sf.params(), 0, ci.params, 0, sf.params().length);
        return ci;
    }

    private String getPropertyPart(String namespace, PropBox property, int ident) {
        StringBuilder sb = new StringBuilder();
        int i = ident;
        sb.append(getPropertyJsDoc(namespace, property, i));
        sb.append("\n");
        sb.append(getIndentStr(i));
        sb.append(String.format("Object.defineProperty(this, \"%s\", {\n", property.name));
        sb.append(getIndentStr(++i));
        assert property.readable;
        sb.append("get: function() {\n");
        sb.append(getIndentStr(++i));
        sb.append(String.format("var value = %s.%s;\n", DELEGATE_OBJECT, property.name));
        sb.append(getIndentStr(i));
        sb.append("return P.boxAsJs(value);\n");
        sb.append(getIndentStr(--i));
        sb.append("}");
        if (property.writeable) {
            sb.append(",\n");
            sb.append(getIndentStr(i));
            sb.append("set: function(aValue) {\n");
            sb.append(getIndentStr(++i));
            sb.append(String.format("delegate.%s = P.boxAsJava(aValue);\n", property.name));
            sb.append(getIndentStr(--i));
            sb.append("}\n");
        } else {
            sb.append("\n");
        }
        sb.append(getIndentStr(--i));
        sb.append("});\n");
        return sb.toString();
    }

    private String getMethodPart(String namespace, Method method, int ident) {
        FunctionInfo fi = getFunctionInfo(method.getName(), method);
        StringBuilder sb = new StringBuilder();
        int i = ident;
        sb.append(getMethodJsDoc(namespace, fi.name, fi.jsDoc, ident));
        sb.append("\n");
        sb.append(getIndentStr(i));
        sb.append(String.format("Object.defineProperty(this, \"%s\", {\n", method.getName()));
        sb.append(getIndentStr(++i));
        sb.append("get: function() {\n");
        sb.append(getIndentStr(++i));
        sb.append("return function(");
        StringBuilder params = new StringBuilder();
        String delimiter = "";
        ScriptFunction methodAnnotation = method.getAnnotation(ScriptFunction.class);
        Parameter[] methodParams = method.getParameters();
        for (int p = 0; p < methodParams.length; p++) {
            Parameter param = methodParams[p];
            String pName = param.getName();
            if(methodAnnotation != null && p < methodAnnotation.params().length){
                pName = methodAnnotation.params()[p];
            }
            sb.append(delimiter).append(pName);
            params.append(delimiter).append("P.boxAsJava(").append(pName).append(")");
            if (delimiter.isEmpty()) {
                delimiter = ", ";
            }
        }
        sb.append(") {\n");
        sb.append(getIndentStr(++i));
        sb.append("var value = ")
                .append(DELEGATE_OBJECT)
                .append(".")
                .append(method.getName())
                .append("(")
                .append(params)
                .append(");\n");
        sb.append(getIndentStr(i));
        sb.append("return P.boxAsJs(value);\n");
        sb.append(getIndentStr(--i));
        sb.append("};\n");
        sb.append(getIndentStr(--i));
        sb.append("}\n");
        sb.append(getIndentStr(--i));
        sb.append("});\n");
        return sb.toString();
    }

    private String getPropertyJsDoc(String namespace, PropBox property, int indent) {
        String jsDoc = property.jsDoc == null || property.jsDoc.isEmpty() ? DEFAULT_PROPERTY_JS_DOC : property.jsDoc;
        jsDoc = formJsDoc(jsDoc);
        jsDoc = appendLine2JsDoc(jsDoc, "@property " + property.name);
        jsDoc = appendLine2JsDoc(jsDoc, "@memberOf " + namespace);
        return addIndent(jsDoc, indent);
    }

    private String getMethodJsDoc(String namespace, String methodName, String str, int ident) {
        String jsDoc = str == null || str.isEmpty() ? DEFAULT_METHOD_JS_DOC : str;
        jsDoc = formJsDoc(jsDoc);
        jsDoc = appendLine2JsDoc(jsDoc, "@method " + methodName);
        jsDoc = appendLine2JsDoc(jsDoc, "@memberOf " + namespace);
        return addIndent(jsDoc, ident);
    }

    private static String formJsDoc(String jsDoc) {
        if (!jsDoc.trim().startsWith("/**")) {//NOI18N
            return String.format(JS_DOC_TEMPLATE, jsDoc);
        } else {
            return jsDoc;
        }

    }

    private static String addIndent(String str, int indent) {
        StringBuilder sb = new StringBuilder();
        String[] lines = str.split("\n");//NOI18N
        for (int i = 0; i < lines.length; i++) {
            sb.append(getIndentStr(indent));
            sb.append(lines[i]);
            if (i < lines.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private static String appendLine2JsDoc(String jsDoc, String line) {
        List<String> jsDocLines = new ArrayList(Arrays.asList(jsDoc.split("\n")));//NOI18N
        jsDocLines.add(jsDocLines.size() - 1, " * " + line);//NOI18N
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jsDocLines.size(); i++) {
            sb.append(jsDocLines.get(i));
            if (i < jsDocLines.size() - 1) {
                sb.append("\n");//NOI18N
            }
        }
        return sb.toString();
    }

    private String getPropsAndMethodsPart(Class clazz, int ident) {
        FunctionInfo ci = getJsConstructorInfo(clazz);
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
            sb.append(getPropertyPart(ci.name, property, ident));
            sb.append("\n");//NOI18N
        }
        for (Method method : methods) {
            sb.append(getMethodPart(ci.name, method, ident));
            sb.append("\n");//NOI18N
        }
        return sb.toString();
    }

    protected static class FunctionInfo {

        public FunctionInfo() {
            jsDoc = "";//NOI18N
            params = new String[0];
        }

        public String name;
        public String javaClassName;
        public String[] params;
        public String jsDoc;

        public String getNullParamsStr() {
            if (params.length == 0) {
                return "";//NOI18N
            }
            StringBuilder paramsSb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                paramsSb.append("null, ");//NOI18N
            }
            return paramsSb.toString();
        }

        public String getParamsStr() {
            StringBuilder paramsSb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                paramsSb.append(params[i]);
                if (i < params.length - 1) {
                    paramsSb.append(", ");//NOI18N
                }
            }
            return paramsSb.toString();
        }

        public String getUnwrappedParamsStr() {
            StringBuilder paramsSb = new StringBuilder();
            String template = "P.boxAsJava(%s)";//NOI18N
            for (int i = 0; i < params.length; i++) {
                paramsSb.append(String.format(template, params[i]));
                if (i < params.length - 1) {
                    paramsSb.append(", ");//NOI18N
                }
            }
            return paramsSb.toString();
        }
    }
}
