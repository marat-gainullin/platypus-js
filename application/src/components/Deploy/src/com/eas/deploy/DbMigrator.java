/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.deploy;

import com.eas.client.cache.AppElementFilesException;
import com.eas.client.ClientConstants;
import com.eas.client.DatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.changes.Change;
import com.eas.client.changes.ChangeValue;
import com.eas.client.changes.Update;
import com.eas.client.dataflow.ColumnsIndicies;
import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.metadata.MetadataSynchronizer;
import com.eas.util.FileUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author vv
 */
public class DbMigrator {

    public static final String MTD_SNAPSHOT_MIGRATION_EXT = "xdm"; // NOI18N
    public static final String SQL_BATCH_MIGRATION_EXT = "batch"; // NOI18N
    protected static final String GET_CURRENT_DB_VERSION_SQL = "SELECT VERSION_VALUE FROM " // NOI18N
            + ClientConstants.T_MTD_VERSION;
    private static final String ILLEGAL_VERSIONS_RECORDS_NUMBER_MSG = "Illegal versions records number - only one record allowed."; // NOI18N
    protected static final String LOCKED_MSG = "Migrator is locked.";

    protected DatabasesClient client;
    protected boolean silentMode;
    protected PrintWriter out = new PrintWriter(System.out, true);
    protected PrintWriter err = new PrintWriter(System.err, true);
    protected boolean busy;
    protected File dir;

    public DbMigrator(File aDir, DatabasesClient aClient) {
        super();
        client = aClient;
        dir = aDir;
    }

    /**
     * Sets default messages output
     *
     * @param anOut Print destination
     */
    public void setOut(PrintWriter anOut) {
        out = anOut;
    }

    /**
     * Sets default error messages output
     *
     * @param anErr Print destination
     */
    public void setErr(PrintWriter anErr) {
        err = anErr;
    }

    public void setSilentMode(boolean silent) {
        silentMode = silent;
    }

    public boolean isSilentMode(boolean silent) {
        return silentMode;
    }

    public boolean isBusy() {
        return busy;
    }

    /**
     * Apply all required migrations to the database.
     *
     */
    public void applyMigrations() {
        synchronized (this) {
            if (busy) {
                Logger.getLogger(DbMigrator.class.getName()).log(Level.WARNING, LOCKED_MSG);
                return;
            }
            busy = true;
        }
        try {
            out.println("Migrating of database is started..."); // NOI18N
            applyMigrationsImpl();
            out.println("Migrating of database is complete."); // NOI18N
            out.println();
        } catch (Exception ex) {
            err.println("Database migration error: " + ex.getMessage()); // NOI18N
            Logger.getLogger(DbMigrator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            synchronized (this) {
                busy = false;
            }
        }
    }

    /**
     * Creates database metadata snapshot migration to the current database
     * state.
     *
     */
    public void createDbMetadataMigration() {
        synchronized (this) {
            if (busy) {
                Logger.getLogger(DbMigrator.class.getName()).log(Level.WARNING, LOCKED_MSG);
                return;
            }
            busy = true;
        }
        try {
            out.println("Creating new Db metadata migration..."); // NOI18N
            int migrationNumber = getCurrentDbVersion() + 1;
            String mtdSnapshotPath = getMtdSnapshotFilePath(migrationNumber);
            String sqlBatchPath = getSqlScriptFilePath(migrationNumber);
            if (!new File(mtdSnapshotPath).exists() && !new File(sqlBatchPath).exists()) {
                MetadataSynchronizer.readMetadataSnapshot(client, client.getConnectionSchema(null), mtdSnapshotPath, out);
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
     */
    public void createSqlMigration() {
        synchronized (this) {
            if (busy) {
                Logger.getLogger(DbMigrator.class.getName()).log(Level.WARNING, LOCKED_MSG);
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
                setCurrentDbVersion(migrationNumber);//Still not 100% clear should we do this
                out.println("New SQL migration created to version: " + migrationNumber); // NOI18N
            } else {
                err.format("Migration for next version %d already exists.\n", migrationNumber);
            }
            out.println();
        } catch (AppElementFilesException ex) {
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
                Logger.getLogger(DbMigrator.class.getName()).log(Level.WARNING, LOCKED_MSG);
                return;
            }
            busy = true;
        }
        try {
            assert dir != null;
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
        } catch (AppElementFilesException ex) {
            Logger.getLogger(DbMigrator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            synchronized (this) {
                busy = false;
            }
        }
    }

    public int getCurrentDbVersion() {
        try {
            assert client != null;
            SqlQuery versionQuery = new SqlQuery(client, GET_CURRENT_DB_VERSION_SQL);
            return versionQuery.compile().executeQuery((ResultSet r) -> {
                ColumnsIndicies idxs = new ColumnsIndicies(r.getMetaData());
                if (r.next()) {
                    Object oVersionNumber = r.getObject(idxs.find(ClientConstants.F_VERSION_VALUE));
                    if (oVersionNumber instanceof Number) {
                        return ((Number) oVersionNumber).intValue();
                    } else {
                        return 0;
                    }
                } else {
                    throw new AppElementFilesException(ILLEGAL_VERSIONS_RECORDS_NUMBER_MSG);
                }
            }, null, null);
        } catch (Exception ex) {
            Logger.getLogger(DbMigrator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void setCurrentDbVersion(int aNewValue) {
        try {
            assert client != null;
            int oldValue = getCurrentDbVersion();
            Update update = new Update(ClientConstants.T_MTD_VERSION);
            update.keys = new ChangeValue[]{new ChangeValue(ClientConstants.F_VERSION_VALUE, oldValue, DataTypeInfo.NUMERIC)};
            update.data = new ChangeValue[]{new ChangeValue(ClientConstants.F_VERSION_VALUE, aNewValue, DataTypeInfo.NUMERIC)};
            client.commit(Collections.singletonMap((String) null, Collections.singletonList(update)), null, null);
        } catch (Exception ex) {
            Logger.getLogger(DbMigrator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void applyMigrationsImpl() throws AppElementFilesException, ParserConfigurationException, SAXException, IOException {
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
                            MetadataSynchronizer.applyMetadataSnapshot(client, client.getConnectionSchema(null), m.getAbsolutePath(), dumpDir, out);
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
    private List<File> listActualMigrations(Integer currentDbVersion) throws AppElementFilesException {
        TreeMap<Integer, File> migrations = new TreeMap<>();
        File[] migrationFiles = dir.listFiles(new MigrationsFilesFilter());
        for (File f : migrationFiles) {
            Integer i = parseInt(FileUtils.removeExtension(f.getName()), null);
            if (i != null && (currentDbVersion == null || i > currentDbVersion)) {
                File m = migrations.get(i);
                if (m == null) {
                    migrations.put(i, f);
                } else {
                    throw new AppElementFilesException("Ambigious migrations file:" + f.getAbsolutePath()); // NOI18N 
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
        DataSource ds = GeneralResourceProvider.getInstance().getPooledDataSource(client.getDefaultDatasourceName());
        try (Connection connection = ds.getConnection()) {
            SqlDriver.applyScript(FileUtils.readString(sqlScriptFile, PlatypusFiles.DEFAULT_ENCODING), connection);
        }
    }

    private String createSqlScriptFile(String path) throws AppElementFilesException {
        File f = new File(path);
        try {
            if (!f.createNewFile()) {
                throw new AppElementFilesException("Can't create new Sql batch file.");
            }
            return f.getAbsolutePath();
        } catch (IOException ex) {
            Logger.getLogger(DbMigrator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String getMtdSnapshotFilePath(int migrationNumber) {
        File f = new File(dir, String.valueOf(migrationNumber) + String.valueOf(FileUtils.EXTENSION_SEPARATOR) + MTD_SNAPSHOT_MIGRATION_EXT);
        return f.getAbsolutePath();
    }

    private String getSqlScriptFilePath(int migrationNumber) {
        File f = new File(dir, Integer.valueOf(migrationNumber).toString() + FileUtils.EXTENSION_SEPARATOR + SQL_BATCH_MIGRATION_EXT);
        return f.getAbsolutePath();
    }

    void initVersioning() throws Exception {
        DatabasesClient.initVersioning(client.obtainDataSource(null));
    }

    private static class MigrationsFilesFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            String ext = FileUtils.getFileExtension(name);
            return MTD_SNAPSHOT_MIGRATION_EXT.equalsIgnoreCase(ext) || SQL_BATCH_MIGRATION_EXT.equalsIgnoreCase(ext);
        }
    }
}
