/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.deploy;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.eas.client.ClientConstants;
import com.eas.client.DbClient;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.queries.SqlQuery;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.metadata.MetadataSynchronizer;
import com.eas.util.FileUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author vv
 */
public class DbMigrator extends BaseDeployer {

    public static final String MTD_SNAPSHOT_MIGRATION_EXT = "xdm"; // NOI18N
    public static final String SQL_BATCH_MIGRATION_EXT = "batch"; // NOI18N
    public static final String PLATYPUS_PROJECT_BOOTSTRAP_DIR = "db"; // NOI18N
    protected static final String GET_CURRENT_DB_VERSION_SQL = "SELECT VERSION_VALUE FROM " // NOI18N
            + ClientConstants.T_MTD_VERSION;
    private static final String ILLEGAL_VERSIONS_RECORDS_NUMBER_MSG = "Illegal versions records number - only one record allowed."; // NOI18N
    private File bootstrapDirectory;

    public DbMigrator(String aProjectPath) {
        super(aProjectPath);
        init();
    }

    public DbMigrator(File aProjectDir, DbClient aClient) {
        super(aProjectDir, aClient);
        init();
    }

    private void init() {
        bootstrapDirectory = new File(projectDir, PLATYPUS_PROJECT_BOOTSTRAP_DIR);
        if (!bootstrapDirectory.isDirectory()) {
            throw new IllegalArgumentException("Bootstrap path is not for directory: " + bootstrapDirectory.getAbsolutePath()); // NOI18N 
        }
    }

    /**
     * Apply all required migrations to the database.
     *
     */
    public void applyMigrations() {
        synchronized (this) {
            if (busy) {
                Logger.getLogger(Deployer.class.getName()).log(Level.WARNING, LOCKED_MSG);
                return;
            }
            busy = true;
        }
        try {
            out.println("Migrating database started..."); // NOI18N
            checkSettings();
            checkDbClient();
            applyMigrationsImpl();
            out.println("Migrating database completed."); // NOI18N
            out.println();
        } catch (Exception ex) {
            err.println("Migrating database error: " + ex.getMessage()); // NOI18N
            Logger.getLogger(DbMigrator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            synchronized (this) {
                busy = false;
            }
        }
    }

    /**
     * Creates database meta-data snapshot migration to the current database
     * state.
     *
     * @return Path to the new file
     */
    public void createDbMetadataMigration() {
        synchronized (this) {
            if (busy) {
                Logger.getLogger(Deployer.class.getName()).log(Level.WARNING, LOCKED_MSG);
                return;
            }
            busy = true;
        }
        try {
            out.println("Creating new Db metadata migration..."); // NOI18N
            checkSettings();
            checkDbClient();
            int migrationNumber = getCurrentDbVersion() + 1;
            String mtdSnapshotPath = getMtdSnapshotFilePath(migrationNumber);
            String sqlBatchPath = getSqlScriptFilePath(migrationNumber);
            if (!new File(mtdSnapshotPath).exists() && !new File(sqlBatchPath).exists()) {
                MetadataSynchronizer.readMetadataSnapshot(client, mtdSnapshotPath, out);
                setCurrentDbVersion(migrationNumber);
                out.println("New Db metadata migration created to version: " + migrationNumber);
            } else {
                err.format("Migration for next version %d already exists.\n", migrationNumber);
            }
            out.println();
        } catch (Exception ex) {
            Logger.getLogger(DbMigrator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            synchronized (this) {
                busy = false;
            }
        }
    }

    /**
     * Creates new SQL migration batch script file.
     *
     * @return Path to the new file
     */
    public void createSqlMigration() {
        synchronized (this) {
            if (busy) {
                Logger.getLogger(Deployer.class.getName()).log(Level.WARNING, LOCKED_MSG);
                return;
            }
            busy = true;
        }
        try {
            out.println("Creating empty SQL migration file..."); // NOI18N
            int migrationNumber = getCurrentDbVersion() + 1;
            String mtdSnapshotPath = getMtdSnapshotFilePath(migrationNumber);
            String sqlBatchPath = getSqlScriptFilePath(migrationNumber);
            if (!new File(mtdSnapshotPath).exists() && !new File(sqlBatchPath).exists()) {
                createSqlScriptFile(sqlBatchPath);
                setCurrentDbVersion(migrationNumber);
                out.println("New SQL migration created to version: " + migrationNumber); // NOI18N
            } else {
                err.format("Migration for next version %d already exists.\n", migrationNumber);
            }
            out.println();
        } catch (DeployException ex) {
            Logger.getLogger(DbMigrator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            synchronized (this) {
                busy = false;
            }
        }
    }

    /**
     * Removes unused database metadata snapshots.
     */
    public void cleanup() {
        synchronized (this) {
            if (busy) {
                Logger.getLogger(Deployer.class.getName()).log(Level.WARNING, LOCKED_MSG);
                return;
            }
            busy = true;
        }
        try {
            assert bootstrapDirectory != null;
            out.println("Cleanup migrations directory..."); // NOI18N
            List<File> filesList = listActualMigrations(null);
            for (int i = 0; i < filesList.size(); i++) {
                File m = filesList.get(i);
                if (MTD_SNAPSHOT_MIGRATION_EXT.equals(FileUtils.getFileExtension(m))
                        && (i < filesList.size() - 1)
                        && MTD_SNAPSHOT_MIGRATION_EXT.equals(FileUtils.getFileExtension(filesList.get(i + 1)))) {
                    m.delete();
                }
            }
            out.println("Cleanup migrations directory complete."); // NOI18N
            out.println();
        } catch (DeployException ex) {
            Logger.getLogger(DbMigrator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            synchronized (this) {
                busy = false;
            }
        }
    }

    public Integer getCurrentDbVersion() {
        try {
            assert client != null;
            SqlQuery versionQuery = new SqlQuery(client, GET_CURRENT_DB_VERSION_SQL);
            Rowset rs = versionQuery.compile().executeQuery();
            if (rs.size() != 1) {
                throw new DeployException(ILLEGAL_VERSIONS_RECORDS_NUMBER_MSG);
            }
            rs.first();
            return rs.getInt(rs.getFields().find(ClientConstants.F_VERSION_VALUE));
        } catch (Exception ex) {
            Logger.getLogger(DbMigrator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void setCurrentDbVersion(int aVersion) {
        try {
            assert client != null;
            SqlQuery versionQuery = new SqlQuery(client, GET_CURRENT_DB_VERSION_SQL);
            versionQuery.setEntityId(ClientConstants.T_MTD_VERSION);
            Rowset rs = versionQuery.compile().executeQuery();
            if (rs.size() != 1) {
                throw new DeployException(ILLEGAL_VERSIONS_RECORDS_NUMBER_MSG);
            }
            rs.getFields().get(ClientConstants.F_VERSION_VALUE).setPk(true);
            Row r = rs.getRow(1);
            r.setColumnObject(rs.getFields().find(ClientConstants.F_VERSION_VALUE), aVersion);
            client.commit(null);
        } catch (Exception ex) {
            client.rollback(null);
            Logger.getLogger(DbMigrator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void applyMigrationsImpl() throws DeployException, ParserConfigurationException, SAXException, IOException {
        String dumpDir = System.getProperty("netbeans.user") + "/var/log/"; //NOI18N
        File dumpDirFile = new File(dumpDir);
        if (!dumpDirFile.exists() || !dumpDirFile.isDirectory()) {
            dumpDir = null;
        }
        int currentVersion = getCurrentDbVersion();
        List<File> filesList = listActualMigrations(currentVersion);
        if (filesList.isEmpty()) {
            out.format("No actual migrations for current database schema version: %s\n", currentVersion); // NOI18N
        }
        for (int i = 0; i < filesList.size(); i++) {
            File m = filesList.get(i);
            int migrationNumber = parseInt(FileUtils.removeExtension(m.getName()), -1);
            assert migrationNumber >= 0;
            try {
                out.format("Migration %d started..\n", migrationNumber); // NOI18N 
                switch (FileUtils.getFileExtension(m)) {
                    case MTD_SNAPSHOT_MIGRATION_EXT:
                        if (i == filesList.size() - 1 || SQL_BATCH_MIGRATION_EXT.equals(FileUtils.getFileExtension(filesList.get(i + 1)))) {
                            MetadataSynchronizer.applyMetadataSnapshot(client, m.getAbsolutePath(), dumpDir, out);
                            out.format("Metadata migration %d applied to the database.\n", migrationNumber); // NOI18N 
                        } else {
                            out.format("Metadata migration %d skipped.\n", migrationNumber); // NOI18N 
                        }
                        break;
                    case SQL_BATCH_MIGRATION_EXT:
                        applySqlScript(m);
                        out.format("Sql migration %d applied to the database.\n", migrationNumber); // NOI18N 
                        break;
                }
                setCurrentDbVersion(migrationNumber);
            } catch (Exception ex) {
                Logger.getLogger(DbMigrator.class.getName()).log(Level.WARNING, null, ex);
                err.format("Metadata migration %d error: %s\n", migrationNumber, ex.getMessage()); // NOI18N 
                break;
            }
        }
    }

    // Ordering is important for method result
    private List<File> listActualMigrations(Integer currentDbVersion) throws DeployException {
        TreeMap<Integer, File> migrations = new TreeMap<>();
        File[] migrationFiles = bootstrapDirectory.listFiles(new MigrationsFilesFilter());
        for (File f : migrationFiles) {
            Integer i = parseInt(FileUtils.removeExtension(f.getName()), null);
            if (i != null && (currentDbVersion == null || i > currentDbVersion)) {
                File m = migrations.get(i);
                if (m == null) {
                    migrations.put(i, f);
                } else {
                    throw new DeployException("Ambigious migrations file:" + f.getAbsolutePath()); // NOI18N 
                }
            }
        }
        List<File> filesList = new ArrayList<>();
        for (Integer i : migrations.keySet()) {
            filesList.add(migrations.get(i));
        }
        return filesList;
    }

    private Integer parseInt(String str, Integer defaultValue) {
        Integer val = defaultValue;
        try {
            val = Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            // no-op
        }
        return val;
    }

    private void applySqlScript(File sqlScriptFile) throws Exception {
        DbConnectionSettings.registerDrivers(DbConnectionSettings.readDrivers().values());
        try (Connection connection = DriverManager.getConnection(settings.getDbSettings().getUrl(), settings.getDbSettings().getInfo())) {
            SqlDriver.applyScript(FileUtils.readString(sqlScriptFile, PlatypusFiles.DEFAULT_ENCODING), connection);
        }
    }

    private String createSqlScriptFile(String path) throws DeployException {
        File f = new File(path);
        try {
            if (!f.createNewFile()) {
                throw new DeployException("Can't create new Sql batch file.");
            }
            return f.getAbsolutePath();
        } catch (IOException ex) {
            Logger.getLogger(DbMigrator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String getMtdSnapshotFilePath(int migrationNumber) {
        File f = new File(bootstrapDirectory, String.valueOf(migrationNumber) + String.valueOf(FileUtils.EXTENSION_SEPARATOR) + MTD_SNAPSHOT_MIGRATION_EXT);
        return f.getAbsolutePath();
    }

    private String getSqlScriptFilePath(int migrationNumber) {
        File f = new File(bootstrapDirectory, Integer.valueOf(migrationNumber).toString() + FileUtils.EXTENSION_SEPARATOR + SQL_BATCH_MIGRATION_EXT);
        return f.getAbsolutePath();
    }

    private static class MigrationsFilesFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            String ext = FileUtils.getFileExtension(name);
            return MTD_SNAPSHOT_MIGRATION_EXT.equalsIgnoreCase(ext) || SQL_BATCH_MIGRATION_EXT.equalsIgnoreCase(ext);
        }
    }
}
