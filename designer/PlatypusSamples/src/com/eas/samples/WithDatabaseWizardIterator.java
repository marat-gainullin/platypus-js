package com.eas.samples;

import com.eas.designer.explorer.project.PlatypusProjectSettingsImpl;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.openide.filesystems.FileUtil;
import org.openide.util.EditableProperties;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author mg
 */
public class WithDatabaseWizardIterator extends PlatypusSamplesWizardIterator {

    public static WithDatabaseWizardIterator createIterator() {
        return new WithDatabaseWizardIterator();
    }

    @Override
    protected void processProjectProperties(EditableProperties aGeneralProperties, EditableProperties aPrivateProperties) {
        super.processProjectProperties(aGeneralProperties, aPrivateProperties);
        String projectName = (String) wiz.getProperty(PlatypusSamples.NAME);
        Path projectDir = Paths.get(Utilities.toURI(FileUtil.normalizeFile((File) wiz.getProperty(PlatypusSamples.PROJ_DIR))));
        String dataFileLocation = projectDir.toString() + File.separator + "data";
        Optional<DatabaseConnection> alreadyDataSource = Arrays.asList(ConnectionManager.getDefault().getConnections()).stream()
                .filter(aConnection -> aConnection.getDatabaseURL().contains("jdbc:h2:tcp") && aConnection.getDatabaseURL().contains(dataFileLocation))
                .findAny();
        if (alreadyDataSource.isPresent()) {
            aPrivateProperties.setProperty(PlatypusProjectSettingsImpl.DEFAULT_DATA_SOURCE_ELEMENT_KEY, alreadyDataSource.get().getDisplayName());
        } else {
            String dataSourceName = projectName;
            Set<String> connections = Arrays.asList(ConnectionManager.getDefault().getConnections())
                    .stream()
                    .map(aConnection -> aConnection.getDisplayName())
                    .collect(Collectors.toSet());
            int counter = 0;
            while (connections.contains(dataSourceName)) {
                dataSourceName = projectName + "_" + ++counter;
            }
            JDBCDriver[] h2Drivers = JDBCDriverManager.getDefault().getDrivers("org.h2.Driver");
            if (h2Drivers != null && h2Drivers.length > 0) {
                try {
                    ConnectionManager.getDefault().addConnection(DatabaseConnection.create(h2Drivers[0], "jdbc:h2:tcp://localhost/" + dataFileLocation, "sa", "public", "sa", true, dataSourceName));
                    aPrivateProperties.setProperty(PlatypusProjectSettingsImpl.DEFAULT_DATA_SOURCE_ELEMENT_KEY, dataSourceName);
                } catch (DatabaseException ex) {
                    Logger.getLogger(WithDatabaseWizardIterator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
