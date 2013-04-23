/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

/**
 *
 * @author AB
 */
public class UpdaterConstants {
    public static final String LOGGER_NAME = "main_log";
    //cmd commands
    public static final String CMD_SWITCHS_PREFIX = "-";
    public static final String CMD_LAF_CONFIG = "laf";
    public static final String CMD_URL_CONFIG = "curl";
    public static final String CMD_URL_UPDATE = "uurl";
    public static final String CMD_CONFIG_NAME = "cname";
    public static final String CMD_TMP_UPDATE_NAME = "uname";
    public static final String CMD_CURRENT_PATH = "path";
    public static final String CMD_RUN_COMMAND = "wrun";
    
    public static final String ROOT_NAME = "update";
    public static final String VERSION_NAME = "version";
    public static final String FILE_NAME = "file_name";
    public static final String COMMENT = "comment";
    public static final String NEWS = "what_new";
    public static final int EQUALS = 0;
    public static final int NOT_EQUALS = 2;
    public static final int FATAL_NOT_EQUALS = 4;
    
    public static final byte NOT_NEED_UPDATE = 0;
    public static final byte NEED_UPDATE = 10;
    public static final byte ERROR = 2;

    public static final String UPDATER_FIND_LABEL = "Updater";
    public static final String COMMAND_CHECK_NEW_VERSION = "newversion";
    public static final String COMMAND_DO_UPDATE = "update";
    public static final String TMP_FILE = "tmp.zip";
    public static final String SINGLE_QUOTE = "\'";
    public static final String DOUBLE_QUOTE = "\"";
    public static final char SLASH_CHAR = '/';
    public static final char BACKSLASH_CHAR = '\\';

    public static final int BUFFER_SIZE = 64*1024;
    
    public static final String ERROR_RUN_COMMAND_FORMAT =  
        "Using \"Updater\":java -jar Updater <command> -laf <value> -curl <value> -uurl <value> -cname <value>\n"
        + "                                  -uname <value> -path <value> -wrun <value>\n"
        + "Parametrs:\n"
        + "         <command>        [newversion, update] What Updater will do now." 
        + "         -laf <value>     UIManager class path.\n" 
        + "         -curl <value>    URL to compare files version.\n"
        + "         -uurl <value>    URL to download new files for application.\n"
        + "         -cname <value>   Name of your file containing application version information.\n"
        + "         -uname <value>   Name of file to download to update the application.\n"
        + "         -path <value>    Current directory. Use if Updater located not in application dir.\n"
        + "         -wrun <value>    What run to start platypus. (deprecated)\n";

}
