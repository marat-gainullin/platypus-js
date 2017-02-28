package com.eas.script;

import com.eas.client.cache.PlatypusFiles;
import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.client.settings.SettingsConstants;
import com.eas.util.FileUtils;
import com.eas.util.PropertiesUtils;
import com.eas.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 * The utility application to convert JavaScript API classes with
 *
 * &#64;ScritpFunction annotation to pure JavaScript objects.
 *
 * @author vv
 */
public class Classes2Scripts {

    public static final String CMD_SWITCHS_PREFIX = "-";//NOI18N
    public static final String CLASS_PATH_CMD_SWITCH = "source";//NOI18N
    public static final String DESTINATION_DIRECTORY_CMD_SWITCH = "dest";//NOI18N

    private static final String JAVA_CLASS_FILE_EXT = ".class";//NOI18N
    private static final String CONSTRUCTOR_TEMPLATE = getStringResource("constructorTemplate.js");//NOI18N

    private static final int DEFAULT_IDENTATION_WIDTH = 4;
    private static final int CONSTRUCTOR_IDENT_LEVEL = 1;

    private static final String DEPS_TAG = "${Deps}";
    private static final String DEPS_RESULTS_TAG = "${DepsResults}";
    private static final String NAME_TAG = "${Name}";//NOI18N
    private static final String JAVA_TYPE_TAG = "${Type}";//NOI18N
    private static final String PARAMS_TAG = "${Params}";//NOI18N
    private static final String NULL_PARAMS_TAG = "${NullParams}";//NOI18N
    private static final String UNWRAPPED_PARAMS_TAG = "${UnwrappedParams}";//NOI18N
    private static final String MAX_ARGS_TAG = "${MaxArgs}";//NOI18N
    private static final String BODY_TAG = "${Body}";
    private static final String PROPERTIES_TAG = "${Props}";//NOI18N
    private static final String METHODS_TAG = "${Methods}";
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

    private String checkScriptObject(Class clazz, String name) {
        ScriptObj ann = (ScriptObj) clazz.getAnnotation(ScriptObj.class);
        if (ann != null && ann.name() != null && !ann.name().isEmpty()) {
            return ann.name();
        } else {
            return name;
        }
    }

    protected static class MethodedPropBox extends PropertiesUtils.PropBox {

        public Method method;
        public String apiName;

        public MethodedPropBox() {
            super();
        }

    }

    private static Classes2Scripts convertor;
    private final List<File> classPaths = new ArrayList<>();
    private File destDirectory;

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
        if (destDirectory == null || !destDirectory.exists()) {
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
            if (c.isDirectory()) {
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
            //createDepsFile();
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
                    .log(Level.FINE, "Processing jar: {0}", new String[]{jarFile.getAbsolutePath()});
            URLClassLoader cl = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, this.getClass().getClassLoader());
            List<File> jarApiFiles = new ArrayList<>();
            List<String> jarApiClasses = new ArrayList<>();
            File subDir = new File(destDirectory, FileNameSupport.getFileName(FileUtils.removeExtension(jarFile.getName())));
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
                                if (!subDir.exists()) {
                                    subDir.mkdir();
                                }
                                Logger.getLogger(Classes2Scripts.class.getName())
                                        .log(Level.FINE, "\tClass name: {0}", new String[]{className});
                                File resultFile = new File(subDir, FileNameSupport.getFileName(jsConstructor.name) + PlatypusFiles.JAVASCRIPT_FILE_END); //NOI18N
                                FileUtils.writeString(resultFile, js, SettingsConstants.COMMON_ENCODING);
                                jarApiFiles.add(resultFile);
                                jarApiClasses.add(jsConstructor.name);
                            }
                        }
                    }
                } catch (NoClassDefFoundError ex) {
                    //NO-OP
                }
            }
            if (!jarApiFiles.isEmpty()) {
                StringBuilder apiDeps = new StringBuilder();
                apiDeps.append("define([\n");
                StringBuilder deps = new StringBuilder();
                StringBuilder depsRes = new StringBuilder();
                StringBuilder moduleItems = new StringBuilder();
                for (int i = 0; i < jarApiFiles.size(); i++) {
                    File jarApiFile = jarApiFiles.get(i);
                    String apiClass = jarApiClasses.get(i);
                    if (i == 0) {
                        deps.append(getIndentStr(1)).append("  ");
                        depsRes.append(getIndentStr(1)).append("  ");
                        moduleItems.append(getIndentStr(2)).append("  ");
                    } else {
                        deps.append(getIndentStr(1)).append(", ");
                        depsRes.append(getIndentStr(1)).append(", ");
                        moduleItems.append(getIndentStr(2)).append(", ");
                    }
                    String includeName = jarApiFile.getName();
                    if (includeName.toLowerCase().endsWith(PlatypusFiles.JAVASCRIPT_FILE_END)) {
                        includeName = includeName.substring(0, includeName.length() - PlatypusFiles.JAVASCRIPT_FILE_END.length());
                    }
                    deps.append("'./").append(includeName).append("'\n");
                    depsRes.append(apiClass).append("\n");
                    moduleItems.append(apiClass).append(": ").append(apiClass).append("\n");
                }
                apiDeps.append(deps).append("]").append(", function(\n");
                apiDeps.append(depsRes).append(getIndentStr(1)).append("){\n").append(getIndentStr(1)).append("return {\n");
                apiDeps.append(moduleItems).append(getIndentStr(1)).append("};\n");
                apiDeps.append("});\n");
                File depsFile = Paths.get(subDir.toURI()).resolve("index.js").toFile();
                FileUtils.writeString(depsFile, apiDeps.toString(), SettingsConstants.COMMON_ENCODING);
            }
        }
    }

    protected String getClassJs(Class clazz) {
        FunctionInfo ci = getJsConstructorInfo(clazz);
        if (ci.javaClassName.contains("$")) {
            Logger.getLogger(Classes2Scripts.class.getName()).log(Level.WARNING, "Inner class: {0}", ci.javaClassName);
            return null;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            Logger.getLogger(Classes2Scripts.class.getName()).log(Level.WARNING, "Abstract class: {0}", ci.javaClassName);
            return null;
        }
        if (!checkForHasPublished(clazz)) {
            Logger.getLogger(Classes2Scripts.class.getName()).log(Level.WARNING, "HasPublished iterface is not implemented: {0}", clazz.getName());
            return null;
        }
        ///
        List<Method> methods = new ArrayList<>();
        Map<String, MethodedPropBox> props = new HashMap<>();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(ScriptFunction.class)) {
                ScriptFunction propAnn = method.getAnnotation(ScriptFunction.class);
                if (PropertiesUtils.isBeanPatternMethod(method)) {
                    String propName = PropertiesUtils.getPropertyName(method.getName());
                    MethodedPropBox pb = props.get(propName);
                    if (pb == null) {
                        pb = new MethodedPropBox();
                        pb.name = propName;
                        if (propAnn.name() != null && !propAnn.name().isEmpty()) {
                            pb.apiName = propAnn.name();
                        } else {
                            pb.apiName = propName;
                        }
                        pb.method = method;
                        props.put(pb.name, pb);
                    }
                    PropertiesUtils.setPropertyAccessStatus(pb, method.getName());
                    PropertiesUtils.setPropertyReturnType(pb, method);
                    if (pb.jsDoc == null || pb.jsDoc.isEmpty()) {
                        pb.jsDoc = propAnn.jsDoc();
                    }
                } else {
                    methods.add(method);
                }
            }
        }
        //

        if (ci.javaClassName.startsWith("com.eas.client.forms.components")
                || ci.javaClassName.startsWith("com.eas.client.forms.containers")
                || ci.javaClassName.startsWith("com.eas.client.forms.menu")) {
            if (ci.javaClassName.startsWith("com.eas.client.forms.components.model.grid.header")) {
                ci.jsDeps += ", 'common-utils/color', 'common-utils/cursor', 'common-utils/font', './cell-render-event'";
                ci.jsDepsResults += ", Color, Cursor, Font, RenderEvent";
            } else {
                ci.jsDeps += ", 'common-utils/color', 'common-utils/cursor', 'common-utils/font', './action-event', './cell-render-event', './component-event', './focus-event', './item-event', './key-event', './value-change-event'";
                ci.jsDepsResults += ", Color, Cursor, Font, ActionEvent, RenderEvent, ComponentEvent, FocusEvent, ItemEvent, KeyEvent, ValueChangeEvent";
                if (ci.javaClassName.startsWith("com.eas.client.forms.containers")
                        || ci.javaClassName.startsWith("com.eas.client.forms.menu.MenuBar")
                        || ci.javaClassName.startsWith("com.eas.client.forms.menu.Menu")
                        || ci.javaClassName.startsWith("com.eas.client.forms.menu.PopupMenu")) {
                    ci.jsDeps += ", './container-event'";
                    ci.jsDepsResults += ", ContainerEvent";
                }
                if (!ci.javaClassName.equals("com.eas.client.forms.menu.PopupMenu")) {
                    ci.jsDeps += ", './popup-menu'";
                    ci.jsDepsResults += ", PopupMenu";
                }
            }
        }
        if (ModelGrid.class.getName().equals(ci.javaClassName)) {
            ci.jsDeps += ", 'grid/cell-data', './service-grid-column', './check-grid-column', './radio-grid-column', './model-check-box', './model-combo', './model-date', './model-formatted-field', './model-grid-column', './model-spin', './model-text-area'";
            ci.jsDepsResults += ", CellData, RenderEvent, ItemEvent, ServiceGridColumn, CheckGridColumn, RadioGridColumn, ModelCheckBox, ModelCombo, ModelDate, ModelFormattedField, ModelGridColumn, ModelSpin, ModelTextArea";
        }
        String js = CONSTRUCTOR_TEMPLATE
                .replace(DEPS_TAG, ci.jsDeps)
                .replace(DEPS_RESULTS_TAG, ci.jsDepsResults)
                .replace(JAVA_TYPE_TAG, ci.javaClassName)
                .replace(JSDOC_TAG, getConstructorJsDoc(ci))
                .replace(NAME_TAG, checkScriptObject(clazz, ci.name))
                .replace(NULL_PARAMS_TAG, ci.getNullParamsStr())
                .replace(PARAMS_TAG, ci.getParamsStr())
                .replace(DELEGATE_TAG, DELEGATE_OBJECT)
                .replace(UNWRAPPED_PARAMS_TAG, ci.getUnwrappedParamsStr(3))
                .replace(MAX_ARGS_TAG, Integer.toString(ci.params.length))
                .replace(PROPERTIES_TAG, getPropsPart(clazz, ci, props.values(), CONSTRUCTOR_IDENT_LEVEL + 1))
                .replace(METHODS_TAG, getMethodsPart(clazz, ci, methods, CONSTRUCTOR_IDENT_LEVEL));
        js = js.replace(BODY_TAG, "");
        return js;
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

    private static boolean checkForHasPublished(Class clazz) {
        return HasPublished.class.isAssignableFrom(clazz);
    }

    private static String getConstructorJsDoc(FunctionInfo ci) {
        return addIndent(appendLine2JsDoc(formJsDoc(ci.jsDoc), "@constructor " + ci.name + " " + ci.name), CONSTRUCTOR_IDENT_LEVEL);
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
            Logger.getLogger(Classes2Scripts.class.getName()).log(Level.SEVERE, ex.getMessage());
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

    private FunctionInfo getConstructorInfo(String javaType, String defaultName, Executable ae) {
        FunctionInfo fi = getFunctionInfo(defaultName, ae);
        fi.javaClassName = javaType;
        return fi;
    }

    private FunctionInfo getFunctionInfo(String defaultName, Executable ae) {
        FunctionInfo fi = new FunctionInfo();
        ScriptFunction sf = (ScriptFunction) ae.getAnnotation(ScriptFunction.class);
        fi.name = sf.name().isEmpty() ? defaultName : sf.name();
        fi.apiName = sf.name();
        fi.jsDoc = formJsDoc(sf.jsDoc());
        fi.params = new String[sf.params().length];
        fi.nativeParams = ae.getParameters();
        System.arraycopy(sf.params(), 0, fi.params, 0, sf.params().length);
        return fi;
    }

    private String getPropertyPart(String namespace, MethodedPropBox property, int ident) {
        StringBuilder sb = new StringBuilder();
        int i = ident;
        String commonIndent = getIndentStr(i);
        String apiPropName = property.name;
        if (property.apiName != null && !property.apiName.isEmpty()) {
            apiPropName = property.apiName;
        }
        if (property.jsDoc != null && !property.jsDoc.isEmpty()) {
            String[] jsDocLines = property.jsDoc.split("\n");
            for (String jsDocLine : jsDocLines) {
                sb.append(commonIndent).append(jsDocLine).append("\n");
            }
        }
        String propValueSample;
        if (property.typeName != null) {
            switch (property.typeName) {
                case "String":
                    propValueSample = "''";
                    break;
                case "Boolean":
                    propValueSample = "true";
                    break;
                case "Number":
                    propValueSample = "0";
                    break;
                default:
                    propValueSample = "new Object()";
                    break;
            }
        } else {
            propValueSample = "new Object()";
        }
        sb.append(commonIndent).append("this.").append(apiPropName).append(" = ").append(propValueSample).append(";").append("\n");
        sb.append(commonIndent).append("Object.defineProperty(").append("this, \"").append(apiPropName).append("\", {\n");
        sb.append(getIndentStr(++i));
        assert property.readable;
        sb.append("get: function() {\n");
        sb.append(getIndentStr(++i));
        sb.append("var value = ").append(DELEGATE_OBJECT).append(".").append(property.name).append(";\n");
        sb.append(getIndentStr(i));
        if (JSObject.class.isAssignableFrom(property.method.getReturnType())) {
            sb.append("return value;\n");
        } else {
            sb.append("return B.boxAsJs(value);\n");
        }
        sb.append(getIndentStr(--i));
        sb.append("}");
        if (property.writeable) {
            sb.append(",\n");
            sb.append(getIndentStr(i));
            sb.append("set: function(aValue) {\n");
            sb.append(getIndentStr(++i));
            sb.append(DELEGATE_OBJECT).append(".").append(property.name);
            if (JSObject.class.isAssignableFrom(property.method.getReturnType())) {
                sb.append(" = aValue;\n");
            } else {
                sb.append(" = B.boxAsJava(aValue);\n");
            }
            sb.append(getIndentStr(--i));
            sb.append("}\n");
        } else {
            sb.append("\n");
        }
        sb.append(getIndentStr(--i));
        sb.append("});\n");
        /*
         TODO: 
         sb.append(getIndentStr(i)).append("if(!P.").append(namespace).append("){\n");
         sb.append(getPropertyJsDoc(namespace, property, ++i)).append("\n");
         sb.append(getIndentStr(i)).append("P.").append(namespace).append(".prototype.").append(apiPropName).append(" = ").append(getDefaultLiteralOfType(property.typeName)).append(";\n");
         sb.append(getIndentStr(--i)).append("}");
         */
        return sb.toString();
    }

    /*
     private String getDefaultLiteralOfType(String aTypeName) {
     if ("Number".equals(aTypeName)) {
     return "0";
     } else if ("Date".equals(aTypeName)) {
     return "new Date()";
     } else if (aTypeName != null && aTypeName.startsWith("[]")) {
     return aTypeName;
     } else if ("Boolean".equals(aTypeName)) {
     return "true";
     } else if ("String".equals(aTypeName)) {
     return "''";
     } else {
     return "{}";
     }
     }
     */
    private String getMethodPart(String namespace, Method method, int indent) {
        FunctionInfo fi = getFunctionInfo(method.getName(), method);
        StringBuilder sb = new StringBuilder();
        int i = indent;
        String methodName = fi.name;
        if (fi.apiName != null && !fi.apiName.isEmpty()) {
            methodName = fi.apiName;
        }
        sb.append(getMethodJsDoc(namespace, methodName, fi.jsDoc, i)).append("\n");
        sb.append(getIndentStr(i));
        sb.append(namespace).append(".prototype.").append(methodName).append(" = ")
                .append("function(");
        StringBuilder paramsInCall = new StringBuilder();
        StringBuilder formalParams = new StringBuilder();
        String delimiter = "";
        ScriptFunction methodAnnotation = method.getAnnotation(ScriptFunction.class);
        Parameter[] methodParams = method.getParameters();

        for (int p = 0; p < methodParams.length; p++) {
            Parameter param = methodParams[p];
            String pName = param.getName();
            if (methodAnnotation != null && p < methodAnnotation.params().length) {
                pName = methodAnnotation.params()[p];
            }
            formalParams.append(delimiter).append(pName);
            paramsInCall.append(delimiter).append("B.boxAsJava(").append(pName).append(")");
            if (delimiter.isEmpty()) {
                delimiter = ", ";
            }
        }
        sb.append(formalParams);
        sb.append(") {\n");
        sb.append(getIndentStr(++i));
        sb.append("var ").append(DELEGATE_OBJECT).append(" = this.unwrap();\n");
        sb.append(getIndentStr(i));
        sb.append("var value = ")
                .append(DELEGATE_OBJECT)
                .append(".")
                .append(method.getName())
                .append("(")
                .append(paramsInCall)
                .append(");\n");
        sb.append(getIndentStr(i));
        sb.append("return B.boxAsJs(value);\n");
        sb.append(getIndentStr(--i));
        sb.append("};\n");
        return sb.toString();
    }

    private String getPropertyJsDoc(String namespace, MethodedPropBox property, int indent) {
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
            String[] lines = jsDoc.split("\n");
            for (int i = 1; i < lines.length; i++) {
                lines[i] = " " + lines[i].trim();
            }
            return StringUtils.join("\n", lines);
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

    private String getPropsPart(Class clazz, FunctionInfo ci, Collection<MethodedPropBox> props, int ident) {
        StringBuilder sb = new StringBuilder();
        for (MethodedPropBox property : props) {
            sb.append(getPropertyPart(checkScriptObject(clazz, ci.name), property, ident))
                    .append("\n");//NOI18N
        }
        return sb.toString();
    }

    private String getMethodsPart(Class clazz, FunctionInfo ci, Collection<Method> methods, int ident) {
        StringBuilder sb = new StringBuilder();
        Set<String> generatedMethods = new HashSet<>();
        for (Method method : methods) {
            if (generatedMethods.contains(method.getName())) {
                throw new IllegalStateException("API Method \"" + method + "\" is duplicated.");
            }
            sb.append(getMethodPart(checkScriptObject(clazz, ci.name), method, ident));
            sb.append("\n");//NOI18N
            generatedMethods.add(method.getName());
        }
        if ("com.eas.client.forms.Form".equals(clazz.getName())) {
            sb.append(""
                    + "    var FormClass = Java.type(\"com.eas.client.forms.Form\");\n"
                    + "    Object.defineProperty(Form, 'shown', {\n"
                    + "        get: function () {\n"
                    + "            var nativeArray = FormClass.getShownForms();\n"
                    + "            var res = [];\n"
                    + "            for (var i = 0; i < nativeArray.length; i++)\n"
                    + "                res[res.length] = nativeArray[i].getPublished();\n"
                    + "            return res;\n"
                    + "        }\n"
                    + "    });\n"
                    + "\n"
                    + "    Object.defineProperty(Form, 'getShownForm', {\n"
                    + "        value: function (aName) {\n"
                    + "            var shownForm = FormClass.getShownForm(aName);\n"
                    + "            return shownForm !== null ? shownForm.getPublished() : null;\n"
                    + "        }\n"
                    + "    });\n"
                    + "\n"
                    + "    Object.defineProperty(Form, 'onChange', {\n"
                    + "        get: function () {\n"
                    + "            return FormClass.getOnChange();\n"
                    + "        },\n"
                    + "        set: function (aValue) {\n"
                    + "            FormClass.setOnChange(aValue);\n"
                    + "        }\n"
                    + "    });\n"
                    + "\n"
                    + "");
        } else if ("com.eas.gui.ScriptColor".equals(clazz.getName())) {
            sb.append(""
                    + "    Object.defineProperty(Color, \"black\", {value: new Color(0, 0, 0)});\n"
                    + "    Object.defineProperty(Color, \"BLACK\", {value: new Color(0, 0, 0)});\n"
                    + "    Object.defineProperty(Color, \"blue\", {value: new Color(0, 0, 0xff)});\n"
                    + "    Object.defineProperty(Color, \"BLUE\", {value: new Color(0, 0, 0xff)});\n"
                    + "    Object.defineProperty(Color, \"cyan\", {value: new Color(0, 0xff, 0xff)});\n"
                    + "    Object.defineProperty(Color, \"CYAN\", {value: new Color(0, 0xff, 0xff)});\n"
                    + "    Object.defineProperty(Color, \"DARK_GRAY\", {value: new Color(0x40, 0x40, 0x40)});\n"
                    + "    Object.defineProperty(Color, \"darkGray\", {value: new Color(0x40, 0x40, 0x40)});\n"
                    + "    Object.defineProperty(Color, \"gray\", {value: new Color(0x80, 0x80, 0x80)});\n"
                    + "    Object.defineProperty(Color, \"GRAY\", {value: new Color(0x80, 0x80, 0x80)});\n"
                    + "    Object.defineProperty(Color, \"green\", {value: new Color(0, 0xff, 0)});\n"
                    + "    Object.defineProperty(Color, \"GREEN\", {value: new Color(0, 0xff, 0)});\n"
                    + "    Object.defineProperty(Color, \"LIGHT_GRAY\", {value: new Color(0xC0, 0xC0, 0xC0)});\n"
                    + "    Object.defineProperty(Color, \"lightGray\", {value: new Color(0xC0, 0xC0, 0xC0)});\n"
                    + "    Object.defineProperty(Color, \"magenta\", {value: new Color(0xff, 0, 0xff)});\n"
                    + "    Object.defineProperty(Color, \"MAGENTA\", {value: new Color(0xff, 0, 0xff)});\n"
                    + "    Object.defineProperty(Color, \"orange\", {value: new Color(0xff, 0xC8, 0)});\n"
                    + "    Object.defineProperty(Color, \"ORANGE\", {value: new Color(0xff, 0xC8, 0)});\n"
                    + "    Object.defineProperty(Color, \"pink\", {value: new Color(0xFF, 0xAF, 0xAF)});\n"
                    + "    Object.defineProperty(Color, \"PINK\", {value: new Color(0xFF, 0xAF, 0xAF)});\n"
                    + "    Object.defineProperty(Color, \"red\", {value: new Color(0xFF, 0, 0)});\n"
                    + "    Object.defineProperty(Color, \"RED\", {value: new Color(0xFF, 0, 0)});\n"
                    + "    Object.defineProperty(Color, \"white\", {value: new Color(0xFF, 0xff, 0xff)});\n"
                    + "    Object.defineProperty(Color, \"WHITE\", {value: new Color(0xFF, 0xff, 0xff)});\n"
                    + "    Object.defineProperty(Color, \"yellow\", {value: new Color(0xFF, 0xff, 0)});\n"
                    + "    Object.defineProperty(Color, \"YELLOW\", {value: new Color(0xFF, 0xff, 0)});\n"
                    + "");
        } else if ("com.eas.gui.Cursor".equals(clazz.getName())) {
            sb.append(""
                    + "    Object.defineProperty(Cursor, \"CROSSHAIR\", {value: new Cursor(1)});\n"
                    + "    Object.defineProperty(Cursor, \"DEFAULT\", {value: new Cursor(0)});\n"
                    + "    Object.defineProperty(Cursor, \"AUTO\", {value: new Cursor(0)});\n"
                    + "    Object.defineProperty(Cursor, \"E_RESIZE\", {value: new Cursor(11)});\n"
                    + "    Object.defineProperty(Cursor, \"HAND\", {value: new Cursor(12)});\n"
                    + "    Object.defineProperty(Cursor, \"MOVE\", {value: new Cursor(13)});\n"
                    + "    Object.defineProperty(Cursor, \"NE_RESIZE\", {value: new Cursor(7)});\n"
                    + "    Object.defineProperty(Cursor, \"NW_RESIZE\", {value: new Cursor(6)});\n"
                    + "    Object.defineProperty(Cursor, \"N_RESIZE\", {value: new Cursor(8)});\n"
                    + "    Object.defineProperty(Cursor, \"SE_RESIZE\", {value: new Cursor(5)});\n"
                    + "    Object.defineProperty(Cursor, \"SW_RESIZE\", {value: new Cursor(4)});\n"
                    + "    Object.defineProperty(Cursor, \"S_RESIZE\", {value: new Cursor(9)});\n"
                    + "    Object.defineProperty(Cursor, \"TEXT\", {value: new Cursor(2)});\n"
                    + "    Object.defineProperty(Cursor, \"WAIT\", {value: new Cursor(3)});\n"
                    + "    Object.defineProperty(Cursor, \"W_RESIZE\", {value: new Cursor(10)});\n"
                    + "");
        }
        return sb.toString();
    }

    protected static class FunctionInfo {

        public FunctionInfo() {
            jsDoc = "";//NOI18N
            params = new String[]{};
            nativeParams = new Parameter[]{};
        }

        public String name;
        public String apiName;
        public String javaClassName;
        public String jsDeps = "'boxing'";
        public String jsDepsResults = "B";
        public String[] params;
        public Parameter[] nativeParams;
        public String jsDoc;

        public String getNullParamsStr() {
            if (params.length == 0) {
                return "";//NOI18N
            }
            StringBuilder paramsSb = new StringBuilder();
            for (String param : params) {
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

        public String getUnwrappedParamsStr(int indent) {
            StringBuilder argsSb = new StringBuilder();
            if (params.length != nativeParams.length) {
                throw new IllegalStateException("@ScriptFunction annotation 'params' parameter is invalid. Constructor of " + javaClassName);
            }
            for (int argsCount = params.length; argsCount >= 0; argsCount--) {
                if (argsCount > 0) {
                    argsSb.append("arguments.length === ").append(argsCount).append(" ? ");
                }
                StringBuilder paramsSb = new StringBuilder();
                paramsSb.append("new javaClass(");
                for (int i = 0; i < argsCount; i++) {
                    paramsSb.append("B.boxAsJava(").append(params[i]).append(")");
                    if (i < argsCount - 1) {
                        paramsSb.append(", ");//NOI18N
                    }
                }
                paramsSb.append(")");
                argsSb.append(paramsSb);
                if (argsCount > 0) {
                    argsSb.append("\n").append(getIndentStr(indent)).append(": ");
                }
            }
            return argsSb.toString();
        }
    }
}
