/*  
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author AB
 */
public class Updater {

    public static final ResourceBundle res = ResourceBundle.getBundle(Updater.class.getPackage().getName() + ".updatermessages");
    public static UpdProgress upd = null;
    private static String cFUrl = "http://olympic.altsoft.biz/platypus/client/updates/update.xml";
    private static String cFName = "update.xml";
    private static String dsUrl = "http://olympic.altsoft.biz/platypus/client/updates/application.zip";
    private static String tmpFile = "updpl.zip";
    private static String curPath = "../../../../";
    private static String whatRun = "";
    private static String command = "";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Logger log = Logger.getLogger(UpdaterConstants.LOGGER_NAME);
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        try {
            if (args.length < 13) {
                System.out.println(UpdaterConstants.ERROR_RUN_COMMAND_FORMAT);
            }

            String userHome = System.getProperty("user.home");
            String pathHome = FileUpdater.fixFileSeparatorChar(userHome + "\\.platypus\\logs\\Updater_log.log");
            FileHandler h = new FileHandler(pathHome);
            h.setFormatter(new SimpleFormatter());
            log.addHandler(h);
            log.setLevel(Level.ALL);
            try {
                parseArgs(args);
            } catch (IllegalArgumentException e) {
                log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
            AppUpdater au = new AppUpdater(cFName, cFUrl, dsUrl, curPath, tmpFile);
            if (!"".equals(command)) {
                switch (command) {
                    case UpdaterConstants.COMMAND_CHECK_NEW_VERSION: {
                        System.exit(au.checkNewVersion());
                    }
                    case UpdaterConstants.COMMAND_DO_UPDATE: {
                        upd = new UpdProgress();
                        upd.setTitle(res.getString("title"));
                        upd.getCaption().setText(res.getString("caption"));
                        au.setUpdVis(upd);
                        au.doUpdateEx();
                    }
                }
            } else {
                System.out.println("Enter command! Command could not be empty. \n" + UpdaterConstants.ERROR_RUN_COMMAND_FORMAT);
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
    }

    private static void parseArgs(String[] args) throws Exception {
        int i = 0;
        while (i < args.length) {
            if (UpdaterConstants.COMMAND_CHECK_NEW_VERSION.equalsIgnoreCase(args[i])) {
                command = UpdaterConstants.COMMAND_CHECK_NEW_VERSION;
                i += 1;
            } else if (UpdaterConstants.COMMAND_DO_UPDATE.equalsIgnoreCase(args[i])) {
                command = UpdaterConstants.COMMAND_DO_UPDATE;
                i += 1;
            } else if ((UpdaterConstants.CMD_SWITCHS_PREFIX + UpdaterConstants.CMD_LAF_CONFIG).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    try {
                        UIManager.setLookAndFeel(args[i + 1]);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                        Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.WARNING, Updater.res.getString("unsupportedLaf"));
                    }
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Laf classpath syntax: -laf <value>");
                }
            } else if ((UpdaterConstants.CMD_SWITCHS_PREFIX + UpdaterConstants.CMD_URL_CONFIG).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    cFUrl = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Config url syntax: -curl <value>");
                }
            } else if ((UpdaterConstants.CMD_SWITCHS_PREFIX + UpdaterConstants.CMD_CONFIG_NAME).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    cFName = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Config file name syntax: -cname <value>");
                }
            } else if ((UpdaterConstants.CMD_SWITCHS_PREFIX + UpdaterConstants.CMD_URL_UPDATE).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    dsUrl = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Update url syntax: -uurl <value>");
                }
            } else if ((UpdaterConstants.CMD_SWITCHS_PREFIX + UpdaterConstants.CMD_TMP_UPDATE_NAME).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    tmpFile = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Update file name syntax: -uname <value>");
                }
            } else if ((UpdaterConstants.CMD_SWITCHS_PREFIX + UpdaterConstants.CMD_CURRENT_PATH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    curPath = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Program curent path: -path <value>");
                }
            } else if ((UpdaterConstants.CMD_SWITCHS_PREFIX + UpdaterConstants.CMD_RUN_COMMAND).equalsIgnoreCase(args[i])) {
                if (i < args.length - 2) {
                    whatRun = args[i + 1];
                    if ((whatRun.indexOf("cscript") >= 0) || (whatRun.indexOf("wscript") >= 0)) {
                        whatRun += " \"" + args[i + 2] + "\"";
                    } else {
                        whatRun += " " + args[i + 2];
                    }
                    i += 3;
                } else {
                    if (i < args.length - 1) {
                        whatRun = args[i + 1];
                        i += 2;
                    } else {
                        throw new IllegalArgumentException("Command to run: -wrun <value>(if you want to run *.js or *.vbs script use \n"
                                + " -wrun cscript(wscript) \"<value>\")");
                    }
                }
            } else {
                i++;
            }
        }
    }
}
