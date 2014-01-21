/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.deploy;

import com.eas.client.DatabasesClient;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.settings.DbConnectionSettings;
import java.io.File;

/**
 *
 * @author vv
 */
public class DeployApplication {

    private static final String DEPLOY_TARGET_DATASOURCE = "deployTarget";
    public static final String APP_PATH_CMD_SWITCH = "ap";
    public static final String URL_CMD_SWITCH = "url";
    public static final String DBUSER_CMD_SWITCH = "dbuser";
    public static final String DBSCHEMA_CMD_SWITCH = "dbschema";
    public static final String DBPASSWORD_CMD_SWITCH = "dbpassword";
    public static final String DEPLOY_CMD_SWITCH = "deploy";  // NOI18N
    public static final String UNDEPLOY_CMD_SWITCH = "undeploy"; // NOI18N
    public static final String IMPORT_CMD_SWITCH = "import"; // NOI18N
    public static final String APPLY_MIGRATIONS_CMD_SWITCH = "apply"; // NOI18N
    public static final String CREATE_MTD_SNAPSHOT_CMD_SWITCH = "snapshot"; // NOI18N
    public static final String CREATE_SQL_BATCH_CMD_SWITCH = "batch"; // NOI18N
    public static final String CLEANUP_CMD_SWITCH = "clean"; // NOI18N
    public static final String INIT_APP_CMD_SWITCH = "initapp"; // NOI18N
    public static final String INIT_USERS_SPACE_CMD_SWITCH = "initusers"; // NOI18N
    public static final String GET_CURRENT_VERSION_CMD_SWITCH = "getver"; // NOI18N
    public static final String SET_CURRENT_VERSION_CMD_SWITCH = "setver"; // NOI18N
    public static final String CMD_SWITCHS_PREFIX = "-";
    protected static final String USAGE_MSG = "Platypus deploy console.\n\n"
            + "Options:\n\n"
            + CMD_SWITCHS_PREFIX + DEPLOY_CMD_SWITCH + " - deploys application from the <app_path> to the database\n"
            + CMD_SWITCHS_PREFIX + UNDEPLOY_CMD_SWITCH + " - removes application from the database\n"
            + CMD_SWITCHS_PREFIX + IMPORT_CMD_SWITCH + " - imports application sources from the database to the <app_path>\n"
            + CMD_SWITCHS_PREFIX + APPLY_MIGRATIONS_CMD_SWITCH + " - applies migrations set from the project to the database\n"
            + CMD_SWITCHS_PREFIX + CREATE_MTD_SNAPSHOT_CMD_SWITCH + " - creates new database metadata snapshot in project's migrations directory\n"
            + CMD_SWITCHS_PREFIX + CREATE_SQL_BATCH_CMD_SWITCH + " - creates new empty database SQL batch migration file in project's migrations directory\n"
            + CMD_SWITCHS_PREFIX + CLEANUP_CMD_SWITCH + " - performs cleanup - removes unused metadata snapshots\n"
            + CMD_SWITCHS_PREFIX + INIT_APP_CMD_SWITCH + "- checks and initializes application database store if it is not initialized\n"
            + CMD_SWITCHS_PREFIX + INIT_USERS_SPACE_CMD_SWITCH + "- checks and initializes users database store if it is not initialized\n"
            + CMD_SWITCHS_PREFIX + GET_CURRENT_VERSION_CMD_SWITCH + " - prints current database vesion\n"
            + CMD_SWITCHS_PREFIX + SET_CURRENT_VERSION_CMD_SWITCH + " <version>  - sets database version to number set in <version>\n"
            + CMD_SWITCHS_PREFIX + APP_PATH_CMD_SWITCH + " <app_path> - sets application directory\n"
            + CMD_SWITCHS_PREFIX + URL_CMD_SWITCH + " <url> - database JDBC URL\n"
            + CMD_SWITCHS_PREFIX + DBSCHEMA_CMD_SWITCH + " <schema> - database schema\n"
            + CMD_SWITCHS_PREFIX + DBUSER_CMD_SWITCH + " <user_name> - database user name\n"
            + CMD_SWITCHS_PREFIX + DBPASSWORD_CMD_SWITCH + " <password> - database password\n"
            + "\n"
            + "Database connection settings and application component name can be provided in parameters, otherwise read from the settings file.\n";
    private String url;
    private String dbuser;
    private String dbschema;
    private String dbpassword;
    private File platypusAppDir;
    private Deployer deployer;
    private DbMigrator migrator;
    private DeployMode mode = DeployMode.PRINT_HELP;
    private int version;

    public enum DeployMode {

        DEPLOY, UNDEPLOY, IMPORT, APPLY_MIGRATIONS, CREATE_MTD_SNAPSHOT, CREATE_SQL_BATCH, CLEANUP, INITAPP, INITUSERS, GET_CURRENT_VERSION, SET_CURRENT_VERSION, PRINT_HELP
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        DeployApplication da = new DeployApplication();
        da.platypusAppDir = new File("."); // NOI18N
        da.parseArgs(args);
        da.doWork();
    }

    private void parseArgs(String[] args) {
        int i = 0;
        while (i < args.length) {
            if ((CMD_SWITCHS_PREFIX + APP_PATH_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    platypusAppDir = new File(args[i + 1]);
                    if (!platypusAppDir.exists()) {
                        throw new IllegalArgumentException("Application path set by " + CMD_SWITCHS_PREFIX + APP_PATH_CMD_SWITCH + " does not exist.");
                    }
                    if (!platypusAppDir.isDirectory()) {
                        throw new IllegalArgumentException("Application path set by " + CMD_SWITCHS_PREFIX + APP_PATH_CMD_SWITCH + " is not directory.");
                    }
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Url syntax: " + CMD_SWITCHS_PREFIX + URL_CMD_SWITCH + " <value>");
                }
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
            } else if ((CMD_SWITCHS_PREFIX + DEPLOY_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                mode = DeployMode.DEPLOY;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + UNDEPLOY_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                mode = DeployMode.UNDEPLOY;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + IMPORT_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                mode = DeployMode.IMPORT;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + APPLY_MIGRATIONS_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                mode = DeployMode.APPLY_MIGRATIONS;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + CREATE_MTD_SNAPSHOT_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                mode = DeployMode.CREATE_MTD_SNAPSHOT;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + CREATE_SQL_BATCH_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                mode = DeployMode.CREATE_SQL_BATCH;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + CLEANUP_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                mode = DeployMode.CLEANUP;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + INIT_APP_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                mode = DeployMode.INITAPP;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + INIT_USERS_SPACE_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                mode = DeployMode.INITUSERS;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + GET_CURRENT_VERSION_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                mode = DeployMode.GET_CURRENT_VERSION;
                i++;
            } else if ((CMD_SWITCHS_PREFIX + SET_CURRENT_VERSION_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                mode = DeployMode.SET_CURRENT_VERSION;
                version = Integer.parseInt(args[i + 1]);
                i += 2;
            } else {
                throw new IllegalArgumentException("unknown argument: " + args[i]);
            }
        }
    }

    private void doWork() throws Exception {
        try {
            // register our datasource
            if (isDbParamsSetExplicitly()) {
                DbConnectionSettings settings = new DbConnectionSettings();
                settings.setUrl(url);
                settings.setUser(dbuser);
                settings.setPassword(dbpassword);
                GeneralResourceProvider.getInstance().registerDatasource(DEPLOY_TARGET_DATASOURCE, settings);
            } else {
                throw new IllegalArgumentException("Database connection arguments are not set properly");
            }
            switch (mode) {
                case DEPLOY:
                    initDeployer();
                    deployer.deploy();
                    break;
                case UNDEPLOY:
                    initDeployer();
                    deployer.undeploy();
                    break;
                case IMPORT:
                    initDeployer();
                    deployer.importApplication();
                    break;
                case APPLY_MIGRATIONS:
                    initMigrator();
                    migrator.applyMigrations();
                    break;
                case CREATE_MTD_SNAPSHOT:
                    initMigrator();
                    migrator.createDbMetadataMigration();
                    break;
                case CREATE_SQL_BATCH:
                    initMigrator();
                    migrator.createSqlMigration();
                    break;
                case CLEANUP:
                    initMigrator();
                    migrator.cleanup();
                    break;
                case INITAPP:
                    initMigrator();
                    migrator.initApp();
                    break;
                case INITUSERS:
                    initMigrator();
                    migrator.initUsersSpace();
                    break;
                case GET_CURRENT_VERSION:
                    initMigrator();
                    printVersion();
                    break;
                case SET_CURRENT_VERSION:
                    initMigrator();
                    setVersion();
                    break;
                default:
                    printHelpMessage();
                    break;
            }
        } catch (Exception ex) {
            System.out.println("\nDeploy/Migration error: " + ex.getMessage());
        }
    }

    private void initDeployer() throws Exception {
        if (isDbParamsSetExplicitly()) {
            deployer = new Deployer(platypusAppDir, new DatabasesClient(null, DEPLOY_TARGET_DATASOURCE, false));
        } else {
            throw new IllegalArgumentException("Database connection arguments are not set properly");
        }
    }

    private void initMigrator() throws Exception {
        if (isDbParamsSetExplicitly()) {
            migrator = new DbMigrator(platypusAppDir, new DatabasesClient(null, DEPLOY_TARGET_DATASOURCE, false));
        } else {
            throw new IllegalArgumentException("Database connection arguments are not set properly");
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
    
    private void printVersion() {
        System.out.println("Current database version is " + migrator.getCurrentDbVersion()); // NOI18N
    }

    private void setVersion() {
        if (version >= 0) {
            migrator.setCurrentDbVersion(version);
            System.out.println("Database version set to " + version); // NOI18N
        } else {
            System.out.println("Database version must not be negative"); // NOI18N
        }

    }

    private static void printHelpMessage() {
        System.out.print(USAGE_MSG);
    }
}
