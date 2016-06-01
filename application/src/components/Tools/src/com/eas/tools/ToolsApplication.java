/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.tools;

import com.eas.client.DatabasesClient;
import com.eas.client.LocalModulesProxy;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.ScriptDocument;
import com.eas.client.cache.ScriptsConfigs;
import com.eas.client.model.store.Model2XmlDom;
import com.eas.client.resourcepool.BearResourcePool;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.scripts.DependenciesWalker;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.SettingsConstants;
import com.eas.util.FileUtils;
import com.eas.util.JsonUtils;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author vv
 */
public class ToolsApplication {

    private static final String TOOLS_DATASOURCE_NAME = "tools-datasource";
    //command
    public static final String INIT_USERS_SPACE_CMD_SWITCH = "initusers";
    // options
    public static final String CMD_SWITCHS_PREFIX = "-";
    public static final String URL_CMD_SWITCH = "url";
    public static final String DBUSER_CMD_SWITCH = "dbuser";
    public static final String DBSCHEMA_CMD_SWITCH = "dbschema";
    public static final String DBPASSWORD_CMD_SWITCH = "dbpassword";
    // command
    public static final String MINIFY_CMD_SWITCH = "minify";
    public static final String INDEX_CMD_SWITCH = "index";
    // options
    public static final String APP_FOLDER_CMD_SWITCH = "app-folder";
    public static final String PROCESSED_FOLDER_CMD_SWITCH = "processed-folder";
    public static final String PROCESSED_FILE_CMD_SWITCH = "processed-file";
    public static final String MINIFIED_MODEL_CMD_SWITCH = "minified-models";
    public static final String MINIFIED_LAYOUT_CMD_SWITCH = "minified-layouts";
    public static final String INDEXED_MODULES_CMD_SWITCH = "indexed-modules";
    public static final String FORCE_CMD_SWITCH = "force";
    //
    private static final Map<String, String> MINIFIED_MODEL_TAGS = new HashMap<String, String>() {
        {
            put("entity", "e");
            put("fieldsEntity", "fe");
            put("parametersEntity", "pe");
            put("parameters", "ps");
            put("parameter", "p");
            put("primaryKey", "pr");
            put("relation", "r");
            put("referenceRelation", "rr");
        }
    };
    private static final Map<String, String> MINIFIED_MODEL_ATTRS = new HashMap<String, String>() {
        {
            put("entityId", "ei");
            put("queryId", "qi");
            put("tableDbId", "tbn");
            put("tableSchemaName", "tsn");
            put("tableName", "tn");
            put("leftEntityId", "lei");
            put("leftEntityFieldName", "lef");
            put("leftEntityParameterName", "lep");
            put("rightEntityId", "rei");
            put("rightEntityFieldName", "ref");
            put("rightEntityParameterName", "rep");
            put("name", "n");
            put("schema", "s");
            put("table", "tl");
            put("field", "f");
            put("tableAlias", "ta");
            put("description", "d");
            put("type", "t");
            put("Title", "tt");
            put("nullable", "nl");
            put("isPk", "p");
            put("parameterMode", "pm");
            put("selectionForm", "sf");
            put("defaultValue", "dv");
            put("classHint", "ch");
            put("scalarPropertyName", "spn");
            put("collectionPropertyName", "cpn");
            put("datasource", "ds");
            put("datamodelDbId", "ddi");
            put("datamodelSchemaName", "dsn");
            put("Name", "n");
            put("entityLocationX", null);
            put("entityLocationY", null);
            put("entityWidth", null);
            put("entityHeight", null);
            put("entityIconified", null);
        }
    };
    private static final Map<String, String> MINIFIED_LAYOUT_TAGS = new HashMap<String, String>() {
        {
            put("Label", "lb");
            put("Button", "bt");
            put("DropDownButton", "ddb");
            put("ButtonGroup", "bg");
            put("CheckBox", "cb");
            put("TextArea", "ta");
            put("HtmlArea", "ha");
            put("FormattedField", "ff");
            put("PasswordField", "pf");
            put("ProgressBar", "pb");
            put("RadioButton", "rb");
            put("Slider", "s");
            put("TextField", "tf");
            put("ToggleButton", "tb");
            put("DesktopPane", "dp");
            put("ModelCheckBox", "mcb");
            put("ModelCombo", "mc");
            put("ModelDate", "md");
            put("ModelFormattedField", "mff");
            put("ModelSpin", "msp");
            put("ModelTextArea", "mta");
            put("ModelGrid", "mg");
            put("AnchorsPane", "ap");
            put("BorderPane", "bp");
            put("BoxPane", "bx");
            put("CardPane", "cp");
            put("FlowPane", "fp");
            put("GridPane", "gp");
            put("ScrollPane", "sp");
            put("SplitPane", "spl");
            put("TabbedPane", "tp");
            put("ToolBar", "tl");
            put("Menu", "m");
            put("MenuItem", "mi");
            put("CheckMenuItem", "cmi");
            put("RadioMenuItem", "rmi");
            put("MenuSeparator", "ms");
            put("MenuBar", "mb");
            put("PopupMenu", "pm");
            put("TabbedPaneConstraints", "tpc");
            put("BorderPaneConstraints", "bpc");
            put("CardPaneConstraints", "cpc");
            put("AnchorsPaneConstraints", "apc");
            put("CheckGridColumn", "cgc");
            put("RadioGridColumn", "rgc");
            put("ServiceGridColumn", "sgc");
            put("ModelGridColumn", "mgc");
            put("font", "ft");
        }
    };
    private static final Map<String, String> MINIFIED_LAYOUT_ATTRS = new HashMap<String, String>() {
        {
            put("icon", "i");
            put("text", "tx");
            put("horizontalAlignment", "ha");
            put("verticalAlignment", "va");
            put("iconTextGap", "itg");
            put("horizontalTextPosition", "htp");
            put("verticalTextPosition", "vtp");
            put("name", "n");
            put("editable", "e");
            put("emptyText", "et");
            put("field", "f");
            put("format", "fr");
            put("font", "ft");
            put("background", "bg");
            put("foreground", "fg");
            put("data", "d");
            put("enabled", "en");
            put("focusable", "fc");
            put("opaque", "o");
            put("toolTipText", "ttt");
            put("cursor", "cr");
            put("visible", "v");
            put("nextFocusableComponent", "nfc");
            put("componentPopupMenu", "cpm");
            put("buttonGroup", "bgr");
            put("parent", "p");
            put("tabTitle", "tt");
            put("tabIcon", "ti");
            put("tabTooltipText", "ttp");
            put("place", "pl");
            put("cardName", "cn");
            put("left", "l");
            put("right", "r");
            put("top", "t");
            put("bottom", "b");
            put("width", "w");
            put("height", "h");
            put("sortField", "sf");
            put("title", "tl");
            put("readonly", "ro");
            put("minWidth", "mw");
            put("maxWidth", "mxw");
            put("preferredWidth", "prw");
            put("movable", "m");
            put("resizable", "rs");
            put("selectOnly", "so");
            put("selected", "st");
            put("sortable", "s");
            put("prefWidth", "pw");
            put("prefHeight", "ph");
            put("defaultCloseOperation", "dco");
            put("closable", "cle");
            put("maximizable", "mxe");
            put("minimizable", "mne");
            put("undecorated", "udr");
            put("opacity", "opc");
            put("alwaysOnTop", "aot");
            put("locationByPlatform", "lbp");
            put("labelFor", "lf");
            put("dropDownMenu", "ddm");
            put("valueType", "vt");
            put("minimum", "mm");
            put("value", "vl");
            put("maximum", "mx");
            put("nullable", "nl");
            put("list", "ls");
            put("displayList", "dl");
            put("displayField", "df");
            put("datePicker", "dtp");
            put("timePicker", "tmp");
            put("frozenColumns", "frc");
            put("frozenRows", "frr");
            put("insertable", "ie");
            put("deletable", "de");
            put("headerVisible", "hv");
            put("draggableRows", "dr");
            put("showHorizontalLines", "shl");
            put("showVerticalLines", "svl");
            put("showOddRowsInOtherColor", "soc");
            put("rowsHeight", "rh");
            put("oddRowsColor", "orc");
            put("gridColor", "gc");
            put("parentField", "pf");
            put("childrenField", "cf");
            put("orientation", "on");
            put("wheelScrollingEnabled", "wse");
            put("horizontalScrollBarPolicy", "hsp");
            put("verticalScrollBarPolicy", "vsp");
            put("oneTouchExpandable", "ote");
            put("dividerLocation", "dvl");
            put("dividerSize", "ds");
            put("leftComponent", "lc");
            put("rightComponent", "rc");
            put("tabPlacement", "tp");
            put("style", "stl");
            put("size", "sz");
            put("step", "step");
            put("min", "min");
            put("max", "max");
            put("hgap", "hgap");
            put("vgap", "vgap");
            put("rows", "rows");
            put("columns", "columns");
        }
    };

    protected static final String USAGE_MSG = "Platypus tools console.\n"
            + "Tools:\n"
            + CMD_SWITCHS_PREFIX + INIT_USERS_SPACE_CMD_SWITCH + " - checks and initializes users database store if it is not initialized\n"
            + CMD_SWITCHS_PREFIX + MINIFY_CMD_SWITCH + " - recursively finds, minifies *.layout and *.model files standalone or as parts of modules and concatenates them in a single file (" + CMD_SWITCHS_PREFIX + APP_FOLDER_CMD_SWITCH + " and " + CMD_SWITCHS_PREFIX + PROCESSED_FOLDER_CMD_SWITCH + " and " + CMD_SWITCHS_PREFIX + MINIFIED_MODEL_CMD_SWITCH + " and " + CMD_SWITCHS_PREFIX + MINIFIED_LAYOUT_CMD_SWITCH + " options).\n"
            + CMD_SWITCHS_PREFIX + INDEX_CMD_SWITCH + " - finds AMD and global modules in *.js files recursively and writes platypus.js modules structure as json to a file (" + CMD_SWITCHS_PREFIX + APP_FOLDER_CMD_SWITCH + " and " + CMD_SWITCHS_PREFIX + PROCESSED_FOLDER_CMD_SWITCH + " and " + CMD_SWITCHS_PREFIX + INDEXED_MODULES_CMD_SWITCH + " options).\n"
            + "Options:\n"
            + CMD_SWITCHS_PREFIX + APP_FOLDER_CMD_SWITCH + " <folder-path> - sets application folder. It will bw used to calculate modules ids for modules without annotations.\n"
            + CMD_SWITCHS_PREFIX + PROCESSED_FOLDER_CMD_SWITCH + " <folder-path> - sets folder to be processed by minifier\n"
            + CMD_SWITCHS_PREFIX + PROCESSED_FILE_CMD_SWITCH + " <file-path> - sets file to be processed by minifier\n"
            + CMD_SWITCHS_PREFIX + MINIFIED_MODEL_CMD_SWITCH + " <file-path> - sets file to write minified content of *.model files into\n"
            + CMD_SWITCHS_PREFIX + MINIFIED_LAYOUT_CMD_SWITCH + " <file-path> - sets file to write the minified content of *.layout files into\n"
            + CMD_SWITCHS_PREFIX + INDEXED_MODULES_CMD_SWITCH + " <file-path> - sets file to write modules index into\n"
            + CMD_SWITCHS_PREFIX + FORCE_CMD_SWITCH + " - forces output files to be overwritten without notice\n"
            + CMD_SWITCHS_PREFIX + URL_CMD_SWITCH + " <url> - database JDBC URL\n"
            + CMD_SWITCHS_PREFIX + DBSCHEMA_CMD_SWITCH + " <schema> - database schema\n"
            + CMD_SWITCHS_PREFIX + DBUSER_CMD_SWITCH + " <user-name> - database user name\n"
            + CMD_SWITCHS_PREFIX + DBPASSWORD_CMD_SWITCH + " <password> - database password\n";
    private String url;
    private String dbuser;
    private String dbschema;
    private String dbpassword;
    private Path appFolder;
    private Path processedFolder;
    private Path processedFile;
    private File minifiedModel;
    private File minifiedLayout;
    private File indexedModules;
    private boolean force;
    private Command command = Command.PRINT_HELP;

    // setup documents framework
    protected static DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

    private static Element minifyElement(Document aProcessedDocument, Element aElement, Map<String, String> minifiedTags, Map<String, String> minifiedAttrs) {
        String processedTagName = minifiedTags.containsKey(aElement.getTagName()) ? minifiedTags.get(aElement.getTagName()) : aElement.getTagName();
        Element processedElement = aProcessedDocument.createElement(processedTagName);
        NamedNodeMap attrs = aElement.getAttributes();
        for (int a = 0; a < attrs.getLength(); a++) {
            Node aNode = attrs.item(a);
            if (aNode instanceof Attr) {
                Attr attr = (Attr) aNode;
                String processedAttrName = minifiedAttrs.containsKey(attr.getName()) ? minifiedAttrs.get(attr.getName()) : attr.getName();
                if (processedAttrName != null && !processedAttrName.isEmpty() && attr.getValue() != null && !attr.getValue().isEmpty()) {// there might be ("longName", null) entries to remove an attribute at all.
                    processedElement.setAttribute(processedAttrName, attr.getValue());
                }
            }
        }
        Node child = aElement.getFirstChild();
        while (child != null) {
            if (child instanceof Element) {
                Element processedChild = minifyElement(aProcessedDocument, (Element) child, minifiedTags, minifiedAttrs);
                processedElement.appendChild(processedChild);
            }
            child = child.getNextSibling();
        }
        return processedElement;
    }

    private static final String BUNDLE_NAME_ATTR = "bundle-name";

    private static void minify(Path appFolder, Path aFolder, Path aFile, File aMinifiedModel, File aMinifiedLayout) throws IOException, ParserConfigurationException {
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document modelsBundle;
        Element modelsBundleRoot;
        if (aMinifiedModel != null) {
            modelsBundle = docBuilder.newDocument();
            modelsBundle.setXmlStandalone(true);
            modelsBundleRoot = modelsBundle.createElement(MODELS_BUNDLE_ROOT_TAG);
            modelsBundle.appendChild(modelsBundleRoot);
        } else {
            modelsBundleRoot = null;
            modelsBundle = null;
        }
        Document layoutsBundle;
        Element layoutsBundleRoot;
        if (aMinifiedLayout != null) {
            layoutsBundle = docBuilder.newDocument();
            layoutsBundle.setXmlStandalone(true);
            layoutsBundleRoot = layoutsBundle.createElement(LAYOUTS_BUNDLE_ROOT_TAG);
            layoutsBundle.appendChild(layoutsBundleRoot);
        } else {
            layoutsBundleRoot = null;
            layoutsBundle = null;
        }
        Consumer<Path> doWork = (Path aPath) -> {
            try {
                File file = aPath.toFile();
                String fileName = file.getName();
                String bundleName = appFolder.relativize(aPath).toString();
                bundleName = FileUtils.removeExtension(bundleName).replace(File.separator, "/");
                if (modelsBundle != null && modelsBundleRoot != null && fileName.endsWith("." + PlatypusFiles.MODEL_EXTENSION)) {
                    String fileNameWoExt = fileName.substring(0, fileName.length() - PlatypusFiles.MODEL_EXTENSION.length());
                    Path sqlPath = aPath.resolveSibling(fileNameWoExt + PlatypusFiles.SQL_EXTENSION);
                    if (!sqlPath.toFile().exists()) {
                        try {
                            Path jsPath = aPath.resolveSibling(fileNameWoExt + PlatypusFiles.JAVASCRIPT_EXTENSION);
                            if (jsPath.toFile().exists()) {
                                ScriptDocument scriptDoc = ScriptDocument.parse(FileUtils.readString(jsPath.toFile(), SettingsConstants.COMMON_ENCODING), bundleName);
                                if (scriptDoc.getModules().size() == 1) {
                                    bundleName = scriptDoc.getModules().keySet().iterator().next();
                                }
                            }
                            String modelContent = FileUtils.readString(file, SettingsConstants.COMMON_ENCODING);
                            Document modelDoc = Source2XmlDom.transform(modelContent);
                            Element modelDocRoot = modelDoc.getDocumentElement();
                            Element processedRoot = minifyElement(modelsBundle, modelDocRoot, MINIFIED_MODEL_TAGS, MINIFIED_MODEL_ATTRS);
                            processedRoot.setAttribute(BUNDLE_NAME_ATTR, bundleName);
                            modelsBundleRoot.appendChild(processedRoot);
                        } catch (SAXException | ParserConfigurationException ex) {
                            Logger.getLogger(ToolsApplication.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (layoutsBundle != null && layoutsBundleRoot != null && fileName.endsWith("." + PlatypusFiles.FORM_EXTENSION)) {
                    try {
                        String fileNameWoExt = fileName.substring(0, fileName.length() - PlatypusFiles.FORM_EXTENSION.length());
                        Path jsPath = aPath.resolveSibling(fileNameWoExt + PlatypusFiles.JAVASCRIPT_EXTENSION);
                        if (jsPath.toFile().exists()) {
                            ScriptDocument scriptDoc = ScriptDocument.parse(FileUtils.readString(jsPath.toFile(), SettingsConstants.COMMON_ENCODING), bundleName);
                            if (!scriptDoc.getModules().isEmpty()) {
                                bundleName = scriptDoc.getModules().keySet().iterator().next();
                            }
                        }
                        String layoutContent = FileUtils.readString(file, SettingsConstants.COMMON_ENCODING);
                        Document layoutDoc = Source2XmlDom.transform(layoutContent);
                        Element layoutDocRoot = layoutDoc.getDocumentElement();
                        Element processedRoot = minifyElement(layoutsBundle, (Element) layoutDocRoot, MINIFIED_LAYOUT_TAGS, MINIFIED_LAYOUT_ATTRS);
                        processedRoot.setAttribute(BUNDLE_NAME_ATTR, bundleName);
                        layoutsBundleRoot.appendChild(processedRoot);
                    } catch (SAXException | ParserConfigurationException ex) {
                        Logger.getLogger(ToolsApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        };
        if (aFile != null) {
            doWork.accept(aFile);
        }
        if (aFolder != null) {
            Files.walkFileTree(aFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path aPath, BasicFileAttributes attrs) throws IOException {
                    if (!aPath.toFile().isDirectory()) {
                        doWork.accept(aPath);
                    }
                    return super.visitFile(aPath, attrs);
                }

            });
        }
        if (modelsBundle != null) {
            assert aMinifiedModel != null;
            File file = aMinifiedModel;
            String bundleContent = XmlDom2String.transform(modelsBundle, false);
            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + "." + PlatypusFiles.MODEL_EXTENSION);
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileUtils.writeString(file, bundleContent, SettingsConstants.COMMON_ENCODING);
            System.out.println("Minified and concatenated models (*.model) written to bundle: " + file.getAbsolutePath());
        }
        if (layoutsBundle != null) {
            assert aMinifiedLayout != null;
            File file = aMinifiedLayout;
            String bundleContent = XmlDom2String.transform(layoutsBundle, false);
            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + "." + PlatypusFiles.FORM_EXTENSION);
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileUtils.writeString(file, bundleContent, SettingsConstants.COMMON_ENCODING);
            System.out.println("Minified and concatenated layouts (*.layout) written to bundle: " + file.getAbsolutePath());
        }
    }
    private static final String LAYOUTS_BUNDLE_ROOT_TAG = "layouts-bundle";
    private static final String MODELS_BUNDLE_ROOT_TAG = "models-bundle";

    private static void index(Path appFolder, Path aFolder, File aIndexedModules) throws Exception {
        Path apiFolder = appFolder.resolve("WEB-INF" + File.separator + "classes");
        ScriptsConfigs scriptsConfigs = new ScriptsConfigs();
        ApplicationSourceIndexer indexer = new ApplicationSourceIndexer(appFolder, apiFolder, scriptsConfigs, false, null);
        indexer.rescan();
        List<StringBuilder> modules = new ArrayList<>();
        Files.walkFileTree(aFolder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path aPath, BasicFileAttributes attrs) throws IOException {
                File file = aPath.toFile();
                if (!file.isDirectory() && file.getName().endsWith(PlatypusFiles.JAVASCRIPT_FILE_END)) {
                    try {
                        String relativeResourceName = appFolder.relativize(aPath).toString().replace(File.separator, "/");
                        String defaultModuleName = FileUtils.removeExtension(relativeResourceName);
                        ScriptDocument scriptDoc = scriptsConfigs.get(defaultModuleName, file);
                        String jsContent = FileUtils.readString(file, SettingsConstants.COMMON_ENCODING);
                        DependenciesWalker walker = new DependenciesWalker(jsContent, (String aModuleCandidate) -> {
                            File depFile = indexer.nameToFile(aModuleCandidate);
                            if (depFile != null && depFile.exists()) {
                                if (depFile.getName().endsWith(PlatypusFiles.JAVASCRIPT_FILE_END)) {
                                    return true;
                                } else {
                                    Logger.getLogger(ToolsApplication.class.getName()).log(Level.WARNING, "JavaScript identifier: {0} found that is the same with non-module application element.", aModuleCandidate);
                                }
                            }// ordinary script class
                            return false;
                        });
                        walker.walk();
                        Set<String> queryDependencies = new HashSet<>(walker.getQueryDependencies());
                        List<String> resources = new ArrayList<>();
                        File modelFile = FileUtils.findBrother(file, PlatypusFiles.MODEL_EXTENSION);
                        if (modelFile != null && modelFile.exists()) {
                            String relativeModelResourceName = appFolder.relativize(Paths.get(modelFile.toURI())).toString().replace(File.separator, "/");
                            resources.add(relativeModelResourceName);
                            Document modelDoc = Source2XmlDom.transform(new String(Files.readAllBytes(Paths.get(modelFile.toURI())), SettingsConstants.COMMON_ENCODING));
                            queryDependencies.addAll(LocalModulesProxy.extractQueriesRefs(modelDoc.getDocumentElement(), Model2XmlDom.ENTITY_TAG_NAME, Model2XmlDom.QUERY_ID_ATTR_NAME));
                            queryDependencies.addAll(LocalModulesProxy.extractQueriesRefs(modelDoc.getDocumentElement(), "e", "qi"));
                        }
                        File layoutFile = FileUtils.findBrother(file, PlatypusFiles.FORM_EXTENSION);
                        if (layoutFile != null && layoutFile.exists()) {
                            String relativeLayoutResourceName = appFolder.relativize(Paths.get(layoutFile.toURI())).toString().replace(File.separator, "/");
                            resources.add(relativeLayoutResourceName);
                        }
                        List<StringBuilder> moduleNamesAndProps = new ArrayList<>();
                        if (!queryDependencies.isEmpty()) {
                            moduleNamesAndProps.add(new StringBuilder("entities"));
                            moduleNamesAndProps.add(JsonUtils.as(queryDependencies.toArray(new String[]{})));
                        }
                        if (!scriptDoc.getModules().isEmpty()) {
                            moduleNamesAndProps.add(new StringBuilder("modules"));
                            moduleNamesAndProps.add(JsonUtils.as(scriptDoc.getModules().keySet().toArray(new String[]{})));
                        }
                        if (!resources.isEmpty()) {
                            moduleNamesAndProps.add(new StringBuilder("prefetched"));
                            moduleNamesAndProps.add(JsonUtils.as(resources.toArray(new String[]{})));
                        }
                        if (!walker.getDependencies().isEmpty()) {
                            moduleNamesAndProps.add(new StringBuilder("global-deps"));
                            moduleNamesAndProps.add(JsonUtils.as(walker.getDependencies().toArray(new String[]{})));
                        }
                        if (!walker.getServerDependencies().isEmpty()) {
                            moduleNamesAndProps.add(new StringBuilder("rpc"));
                            moduleNamesAndProps.add(JsonUtils.as(walker.getServerDependencies().toArray(new String[]{})));
                        }
                        StringBuilder module = JsonUtils.o(moduleNamesAndProps.toArray(new StringBuilder[]{}));
                        modules.add(new StringBuilder(relativeResourceName));
                        modules.add(module);
                    } catch (Exception ex) {
                        Logger.getLogger(ToolsApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        StringBuilder jsonModules = JsonUtils.o(modules.toArray(new StringBuilder[]{}));
        StringBuilder js = new StringBuilder();
        js.append("define[\"modules-index\"] = ");
        js.append(jsonModules);
        js.append(";");
        if (!aIndexedModules.exists()) {
            aIndexedModules.createNewFile();
        }
        FileUtils.writeString(aIndexedModules, js.toString(), SettingsConstants.COMMON_ENCODING);
        System.out.println("Modules index generated and written to file: " + aIndexedModules.getAbsolutePath());
    }

    public enum Command {
        INITUSERS, MINIFY, INDEX, PRINT_HELP
    }

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        ToolsApplication ta = new ToolsApplication();
        ta.processedFolder = Paths.get(new File(".").toURI()); // NOI18N
        try {
            ta.parseArgs(args);
            ta.doWork();
        } catch (IllegalArgumentException ex) {
            System.out.println("ERROR. " + ex.getMessage());
            System.exit(1);
        }
    }

    private void parseArgs(String[] args) {
        int i = 0;
        while (i < args.length) {
            if ((CMD_SWITCHS_PREFIX + APP_FOLDER_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    File appFolderFile = new File(args[i + 1]);
                    if (!appFolderFile.exists()) {
                        throw new IllegalArgumentException(appFolderFile.getAbsolutePath() + " does not exist.");
                    }
                    if (!appFolderFile.isDirectory()) {
                        throw new IllegalArgumentException(appFolderFile.getAbsolutePath() + " is not directory.");
                    }
                    appFolder = Paths.get(appFolderFile.toURI());
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Application folder syntax: " + CMD_SWITCHS_PREFIX + APP_FOLDER_CMD_SWITCH + " <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + PROCESSED_FOLDER_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    File pFolder = new File(args[i + 1]);
                    if (!pFolder.exists()) {
                        throw new IllegalArgumentException(pFolder.getAbsolutePath() + " does not exist.");
                    }
                    if (!pFolder.isDirectory()) {
                        throw new IllegalArgumentException(pFolder.getAbsolutePath() + " is not directory.");
                    }
                    processedFolder = Paths.get(pFolder.toURI());
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Processed folder syntax: " + CMD_SWITCHS_PREFIX + PROCESSED_FOLDER_CMD_SWITCH + " <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + PROCESSED_FILE_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    File pFile = new File(args[i + 1]);
                    if (!pFile.exists()) {
                        throw new IllegalArgumentException(pFile.getAbsolutePath() + " does not exist.");
                    }
                    if (pFile.isDirectory()) {
                        throw new IllegalArgumentException(pFile.getAbsolutePath() + " is directory.");
                    }
                    processedFile = Paths.get(pFile.toURI());
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Processed file syntax: " + CMD_SWITCHS_PREFIX + PROCESSED_FILE_CMD_SWITCH + " <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + MINIFIED_MODEL_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    minifiedModel = new File(args[i + 1]);
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Minified models syntax: " + CMD_SWITCHS_PREFIX + MINIFIED_MODEL_CMD_SWITCH + " <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + MINIFIED_LAYOUT_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    minifiedLayout = new File(args[i + 1]);
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Minified layouts syntax: " + CMD_SWITCHS_PREFIX + MINIFIED_LAYOUT_CMD_SWITCH + " <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + INDEXED_MODULES_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    indexedModules = new File(args[i + 1]);
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Indexed modules syntax: " + CMD_SWITCHS_PREFIX + INDEXED_MODULES_CMD_SWITCH + " <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + FORCE_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                force = true;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + URL_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    url = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Url syntax: " + CMD_SWITCHS_PREFIX + URL_CMD_SWITCH + " <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + DBUSER_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    dbuser = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Db user syntax: " + CMD_SWITCHS_PREFIX + DBUSER_CMD_SWITCH + " <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + DBSCHEMA_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    dbschema = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Db schema syntax: " + CMD_SWITCHS_PREFIX + DBSCHEMA_CMD_SWITCH + " <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + DBPASSWORD_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    dbpassword = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Db password syntax: " + CMD_SWITCHS_PREFIX + DBPASSWORD_CMD_SWITCH + " <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + INIT_USERS_SPACE_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                command = Command.INITUSERS;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + MINIFY_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                command = Command.MINIFY;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + INDEX_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                command = Command.INDEX;
                i++;
            } else {
                throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }
        }
    }

    private void doWork() throws Exception {
        switch (command) {
            case MINIFY:
                if (appFolder == null) {
                    throw new IllegalArgumentException("Application folder is not set.");
                }
                if (processedFolder == null && processedFile == null) {
                    throw new IllegalArgumentException("Neither file nor folder to be processed is not set.");
                }
                if (minifiedModel == null && minifiedLayout == null) {
                    throw new IllegalArgumentException("No any file to concatenate minified content into is set.");
                }
                if (!force && minifiedModel.exists()) {
                    throw new IllegalArgumentException(minifiedModel.getAbsolutePath() + " already exists.");
                }
                if (!force && minifiedLayout.exists()) {
                    throw new IllegalArgumentException(minifiedLayout.getAbsolutePath() + " already exists.");
                }
                minify(appFolder, processedFolder, processedFile, minifiedModel, minifiedLayout);
                break;
            case INDEX:
                if (appFolder == null) {
                    throw new IllegalArgumentException("Application folder is not set.");
                }
                if (processedFolder == null) {
                    throw new IllegalArgumentException("Folder to be processed is not set.");
                }
                if (indexedModules == null) {
                    throw new IllegalArgumentException("File to to output modules index into is not set.");
                }
                if (!force && indexedModules.exists()) {
                    throw new IllegalArgumentException(indexedModules.getAbsolutePath() + " already exists.");
                }
                index(appFolder, processedFolder, indexedModules);
                break;
            case INITUSERS: {
                // register our datasource
                if (isDbParamsSetExplicitly()) {
                    DbConnectionSettings settings = new DbConnectionSettings();
                    settings.setUrl(url);
                    settings.setUser(dbuser);
                    settings.setPassword(dbpassword);
                    settings.setSchema(dbschema);
                    GeneralResourceProvider.getInstance().registerDatasource(TOOLS_DATASOURCE_NAME, settings);
                } else {
                    throw new IllegalArgumentException("Database connection arguments are not set properly");
                }
                DatabasesClient client = new DatabasesClient(TOOLS_DATASOURCE_NAME, false, BearResourcePool.DEFAULT_MAXIMUM_SIZE);
                DatabasesClient.initUsersSpace(client.obtainDataSource(null));
            }
            break;
            default:
                printHelpMessage();
                break;
        }
    }

    private boolean isDbParamsSetExplicitly() {
        if (url != null && dbschema != null && dbuser != null && dbpassword != null) {
            return true;
        } else if ((url == null && dbschema == null && dbuser == null && dbpassword == null)) {
            return false;
        }
        throw new IllegalStateException("Some db connection parameters set, but some not.");
    }

    private static void printHelpMessage() {
        System.out.print(USAGE_MSG);
    }
}
